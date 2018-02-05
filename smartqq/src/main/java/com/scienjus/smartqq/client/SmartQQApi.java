package com.scienjus.smartqq.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scienjus.smartqq.constant.ApiURL;
import com.scienjus.smartqq.handler.AbstractContactHandler;
import com.scienjus.smartqq.model.Font;

import cn.ieclipse.smartim.exception.HttpException;
import cn.ieclipse.smartim.exception.LogicException;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Api客户端.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/18.
 */
public class SmartQQApi implements Closeable {
    
    public static boolean DEBUG = true;
    
    // 日志
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SmartQQApi.class);
            
    // 发生ngnix 404 时的重试次数
    private static int retryTimesOnFailed = 3;
    
    // 消息id，这个好像可以随便设置，所以设成全局的
    private static long MESSAGE_ID = 43690001;
    
    // 客户端id，固定的
    private static final long Client_ID = 53999199;
    
    // 消息发送失败重发次数
    private static final long RETRY_TIMES = 5;
    
    // 客户端
    private OkHttpClient client;
    
    // Cookie 管理
    private CookieManager cookieJar = new CookieManager();
    
    // 二维码令牌
    private String qrsig;
    private File workDir;
    
    // 鉴权参数
    private String ptwebqq;
    private String vfwebqq;
    private long uin;
    private String psessionid;
    
    public void setWorkDir(File dir) {
        if (dir == null) {
            throw new NullPointerException("Work dir is null");
        }
        this.workDir = dir;
    }
    
    public SmartQQApi(Proxy proxy) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(cookieJar);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        if (proxy != null) {
            builder.proxy(proxy);
        }
        this.client = builder.build();
    }
    
    /**
     * 登录
     */
    public void login() throws Exception {
        String url = verifyQRCode();
        getPtwebqq(url);
        getVfwebqq();
        getUinAndPsessionid();
        getFriendStatus(); // 修复Api返回码[103]的问题
    }
    
    // 登录流程1：获取二维码
    public String getQRCode() {
        LOGGER.info("开始获取二维码");
        // 本地存储二维码图片
        String filePath;
        try {
            File file = new File(workDir, "smartqq.jpg");
            if (file.getParentFile() != null
                    && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            filePath = file.getAbsolutePath();
            Response response = get(ApiURL.GET_QR_CODE);
            if (response.isSuccessful()) {
                InputStream is = response.body().byteStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                Cookie c = cookieJar.getFirstCookie("qrsig");
                if (c != null) {
                    qrsig = c.value();
                }
                LOGGER.info("二维码已保存在 " + filePath + " 文件中，请打开手机QQ并扫描二维码");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return filePath;
    }
    
    // 用于生成ptqrtoken的哈希函数
    private static int hash33(String s) {
        int e = 0, n = s.length();
        for (int i = 0; n > i; ++i)
            e += (e << 5) + s.charAt(i);
        return 2147483647 & e;
    }
    
    // 登录流程2：校验二维码
    public String verifyQRCode() throws IOException {
        LOGGER.debug("等待扫描二维码");
        
        int maxCount = 30;
        // 阻塞直到确认二维码认证成功
        while (true) {
            sleep(maxCount < 15 ? 3 : 1);
            Response response = get(ApiURL.VERIFY_QR_CODE, hash33(qrsig));
            String result = response.body().string();
            if (result.contains("成功")) {
                for (String content : result.split("','")) {
                    if (content.startsWith("http")) {
                        LOGGER.info("正在登录，请稍后");
                        Cookie cookie = cookieJar.getFirstCookie("ptwebqq");
                        if (cookie != null) {
                            this.ptwebqq = cookie.value();
                        }
                        return content;
                    }
                }
            }
            else if (result.contains("已失效")) {
                LOGGER.info("二维码已失效，尝试重新获取二维码");
                // getQRCode();
                throw new RuntimeException("二维码已失效");
            }
            maxCount--;
            if (maxCount <= 0) {
                throw new RuntimeException("验证二维码超时");
            }
        }
    }
    
    // 登录流程3：获取ptwebqq
    private void getPtwebqq(String url) throws Exception {
        LOGGER.info("开始获取ptwebqq");
        
        Response response = get(ApiURL.GET_PTWEBQQ, url);
        int retryTimes4Vfwebqq = retryTimesOnFailed;
        while (response.code() == 404 && retryTimes4Vfwebqq > 0) {
            response.close();
            response = get(ApiURL.GET_PTWEBQQ, url);
            retryTimes4Vfwebqq--;
        }
        this.ptwebqq = cookieJar.getFirstCookie("ptwebqq").value();
    }
    
    // 登录流程4：获取vfwebqq
    private void getVfwebqq() throws Exception {
        LOGGER.info("开始获取vfwebqq");
        
        Response response = get(ApiURL.GET_VFWEBQQ, ptwebqq);
        int retryTimes4Vfwebqq = retryTimesOnFailed;
        while (response.code() == 404 && retryTimes4Vfwebqq > 0) {
            response.close();
            response = get(ApiURL.GET_VFWEBQQ, ptwebqq);
            retryTimes4Vfwebqq--;
        }
        this.vfwebqq = getJsonObjectResult(response).get("vfwebqq")
                .getAsString();
    }
    
    // 登录流程5：获取uin和psessionid
    private void getUinAndPsessionid() throws Exception {
        LOGGER.info("开始获取uin和psessionid");
        
        JsonObject r = new JsonObject();
        r.addProperty("ptwebqq", ptwebqq);
        r.addProperty("clientid", Client_ID);
        r.addProperty("psessionid", "");
        r.addProperty("status", "online");
        
        Response response = post(ApiURL.GET_UIN_AND_PSESSIONID, r);
        JsonObject result = getJsonObjectResult(response);
        this.psessionid = result.get("psessionid").getAsString();
        this.uin = result.get("uin").getAsLong();
    }
    
    /**
     * 获取群列表
     *
     * @return
     */
    public JsonArray getGroupList() throws Exception {
        LOGGER.info("开始获取群列表");
        
        JsonObject r = new JsonObject();
        r.addProperty("vfwebqq", vfwebqq);
        r.addProperty("hash", hash());
        
        Response response = post(ApiURL.GET_GROUP_LIST, r);
        int retryTimes4Vfwebqq = retryTimesOnFailed;
        while (response.code() == 404 && retryTimes4Vfwebqq > 0) {
            response.close();
            response = post(ApiURL.GET_GROUP_LIST, r);
            retryTimes4Vfwebqq--;
        }
        JsonObject result = getJsonObjectResult(response);
        return (result.getAsJsonArray("gnamelist"));
    }
    
    /**
     * 拉取消息
     *
     */
    public JsonArray pollMessage() throws Exception {
//        if (DEBUG)
//            LOGGER.debug("开始接收消息");
            
        JsonObject r = new JsonObject();
        r.addProperty("ptwebqq", ptwebqq);
        r.addProperty("clientid", Client_ID);
        r.addProperty("psessionid", psessionid);
        r.addProperty("key", "");
        
        Response response = post(ApiURL.POLL_MESSAGE, r);
        JsonArray array = getJsonArrayResult(response);
        return array;
    }
    
    private void addSendParam(JsonObject r, String msg) {
        r.addProperty("content", new Gson().toJson(
                Arrays.asList(msg, Arrays.asList("font", Font.DEFAULT_FONT)))); // 注意这里虽然格式是Json，但是实际是String
        r.addProperty("face", 573);
        r.addProperty("clientid", Client_ID);
        r.addProperty("msg_id", MESSAGE_ID++);
        r.addProperty("psessionid", psessionid);
    }
    
    /**
     * 发送群消息
     *
     * @param groupId
     *            群id
     * @param msg
     *            消息内容
     */
    public void sendMessageToGroup(long groupId, String msg) throws Exception {
        LOGGER.debug("开始发送群消息");
        
        JsonObject r = new JsonObject();
        r.addProperty("group_uin", groupId);
        addSendParam(r, msg);
        
        Response response = postWithRetry(ApiURL.SEND_MESSAGE_TO_GROUP, r);
        checkSendMsgResult(response);
    }
    
    /**
     * 发送讨论组消息
     *
     * @param discussId
     *            讨论组id
     * @param msg
     *            消息内容
     */
    public void sendMessageToDiscuss(long discussId, String msg)
            throws Exception {
        LOGGER.debug("开始发送讨论组消息");
        
        JsonObject r = new JsonObject();
        r.addProperty("did", discussId);
        addSendParam(r, msg);
        
        Response response = postWithRetry(ApiURL.SEND_MESSAGE_TO_DISCUSS, r);
        checkSendMsgResult(response);
    }
    
    /**
     * 发送消息
     *
     * @param friendId
     *            好友id
     * @param msg
     *            消息内容
     */
    public void sendMessageToFriend(long friendId, String msg)
            throws Exception {
        LOGGER.debug("开始发送消息");
        
        JsonObject r = new JsonObject();
        r.addProperty("to", friendId);
        addSendParam(r, msg);
        
        Response response = postWithRetry(ApiURL.SEND_MESSAGE_TO_FRIEND, r);
        checkSendMsgResult(response);
    }
    
    /**
     * 获得讨论组列表
     *
     * @return
     */
    public JsonArray getDiscussList() throws Exception {
        LOGGER.info("开始获取讨论组列表");
        
        Response response = get(ApiURL.GET_DISCUSS_LIST, psessionid, vfwebqq);
        return getJsonObjectResult(response).getAsJsonArray("dnamelist");
    }
    
    /**
     * 获得好友列表（包含分组信息）
     *
     * @return
     */
    public JsonObject getFriendListWithCategory() throws Exception {
        LOGGER.info("开始获取好友列表");
        
        JsonObject r = new JsonObject();
        r.addProperty("vfwebqq", vfwebqq);
        r.addProperty("hash", hash());
        
        Response response = post(ApiURL.GET_FRIEND_LIST, r);
        JsonObject result = getJsonObjectResult(response);
        
        return (result);
    }
    
    /**
     * 获取好友列表
     *
     * @return
     */
    public JsonObject getFriendList() throws Exception {
        LOGGER.info("开始获取好友列表");
        
        JsonObject r = new JsonObject();
        r.addProperty("vfwebqq", vfwebqq);
        r.addProperty("hash", hash());
        
        Response response = post(ApiURL.GET_FRIEND_LIST, r);
        return getJsonObjectResult(response);
    }
    
    /**
     * 获得当前登录用户的详细信息
     *
     * @return
     */
    public JsonObject getAccountInfo() throws Exception {
        LOGGER.info("开始获取登录用户信息");
        
        Response response = get(ApiURL.GET_ACCOUNT_INFO);
        return getJsonObjectResult(response);
    }
    
    /**
     * 获得好友的详细信息
     *
     * @return
     */
    public JsonObject getFriendInfo(long friendId) throws Exception {
        LOGGER.info("开始获取好友信息");
        
        Response response = get(ApiURL.GET_FRIEND_INFO, friendId, vfwebqq,
                psessionid);
        return (getJsonObjectResult(response));
    }
    
    /**
     * 获得最近会话列表
     *
     * @return
     */
    public JsonArray getRecentList() throws Exception {
        LOGGER.info("开始获取最近会话列表");
        
        JsonObject r = new JsonObject();
        r.addProperty("vfwebqq", vfwebqq);
        r.addProperty("clientid", Client_ID);
        r.addProperty("psessionid", "");
        
        Response response = post(ApiURL.GET_RECENT_LIST, r);
        return (getJsonArrayResult(response));
    }
    
    /**
     * 获得qq号
     *
     * @param friendId
     *            用户id
     * @return
     */
    public long getQQById(long friendId) throws Exception {
        LOGGER.debug("开始获取QQ号");
        
        Response response = get(ApiURL.GET_QQ_BY_ID, friendId, vfwebqq);
        return getJsonObjectResult(response).get("account").getAsLong();
    }
    
    /**
     * 获得登录状态
     *
     * @return
     */
    public JsonArray getFriendStatus() throws Exception {
        LOGGER.debug("开始获取好友状态");
        
        Response response = get(ApiURL.GET_FRIEND_STATUS, vfwebqq, psessionid);
        return (getJsonArrayResult(response));
    }
    
    /**
     * 获得群的详细信息
     *
     * @param groupCode
     *            群编号
     * @return
     */
    public JsonObject getGroupInfo(long groupCode) throws Exception {
        LOGGER.info("开始获取群资料");
        
        Response response = get(ApiURL.GET_GROUP_INFO, groupCode, vfwebqq);
        JsonObject result = getJsonObjectResult(response);
        return result;
    }
    
    /**
     * 获得讨论组的详细信息
     *
     * @param discussId
     *            讨论组id
     * @return
     */
    public JsonObject getDiscussInfo(long discussId) throws Exception {
        LOGGER.info("开始获取讨论组资料");
        
        Response response = get(ApiURL.GET_DISCUSS_INFO, discussId, vfwebqq,
                psessionid);
        JsonObject result = getJsonObjectResult(response);
        return result;
    }
    
    // 发送get请求
    private Response get(ApiURL url, Object... params) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url.buildUrl(params))
                .addHeader("User-Agent", ApiURL.USER_AGENT);
                
        if (url.getReferer() != null) {
            builder.addHeader("Referer", url.getReferer());
        }
        
        Request request = builder.tag(getClass()).build();
        Call call = this.client.newCall(request);
        return call.execute();
    }
    
    // 发送post请求
    private Response post(ApiURL url, JsonObject r) throws IOException {
        RequestBody body = new FormBody.Builder().add("r", r.toString())
                .build();
        Request.Builder builder = new Request.Builder().url(url.buildUrl())
                .addHeader("User-Agent", ApiURL.USER_AGENT)
                .addHeader("Origin", url.getOrigin()).post(body);
        if (url.getReferer() != null) {
            builder.addHeader("Referer", url.getReferer());
        }
        Request request = builder.tag(getClass()).build();
        Call call = this.client.newCall(request);
        return call.execute();
    }
    
    // 发送post请求，失败时重试
    private Response postWithRetry(ApiURL url, JsonObject r) throws Exception {
        int times = 0;
        Response response;
        do {
            response = post(url, r);
            times++;
        } while (times < RETRY_TIMES && response.code() != 200);
        return response;
    }
    
    // 获取返回json的result字段（JsonObject类型）
    private static JsonObject getJsonObjectResult(Response response)
            throws Exception {
        return getResponseJson(response).getAsJsonObject("result");
    }
    
    // 获取返回json的result字段（JSONArray类型）
    private static JsonArray getJsonArrayResult(Response response)
            throws Exception {
        return getResponseJson(response).getAsJsonArray("result");
    }
    
    // 检查消息是否发送成功
    private static void checkSendMsgResult(Response response) throws Exception {
        if (response.code() != 200) {
            String msg = String.format("发送失败，Http返回码[%d]", response.code());
            LOGGER.error(msg);
            throw new HttpException(response.code(), response.message());
        }
        JsonObject json = (JsonObject) new JsonParser()
                .parse(response.body().string());
        Integer errCode = json.get("retcode").getAsInt();
        if (errCode != null && errCode == 0) {
            LOGGER.debug("发送成功");
        }
        else {
            String msg = String.format("发送失败，Api返回码[%d]",
                    json.get("retcode").getAsInt());
            LOGGER.error(msg);
            throw new LogicException(json.get("retcode").getAsInt(), msg);
        }
    }
    
    // 检验Json返回结果
    private static JsonObject getResponseJson(Response response)
            throws IOException {
        if (response.code() != 200) {
            throw new HttpException(response.code(),
                    String.format("请求失败，Http返回码[%d]", response.code()));
        }
        JsonObject json = (JsonObject) new JsonParser()
                .parse(response.body().string());
        Integer retCode = json.get("retcode").getAsInt();
        if (retCode != 0) {
            switch (retCode) {
                case 103: {
                    LOGGER.error(
                            "请求失败，Api返回码[103]。你需要进入http://w.qq.com，检查是否能正常接收消息。如果可以的话点击[设置]->[退出登录]后查看是否恢复正常");
                    break;
                }
                case 100100: {
                    LOGGER.error("请求失败，Api返回码[100100]");
                    break;
                }
                default: {
                    LOGGER.error(String.format("请求失败，Api返回码[%d]", retCode));
                }
            }
            throw new LogicException(retCode,
                    String.format("请求失败，Api返回码[%d]", retCode));
        }
        return json;
    }
    
    // hash加密方法
    private String hash() {
        return hash(uin, ptwebqq);
    }
    
    // 线程暂停
    private static void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            // 忽略InterruptedException
        }
    }
    
    // hash加密方法
    private static String hash(long x, String K) {
        int[] N = new int[4];
        for (int T = 0; T < K.length(); T++) {
            N[T % 4] ^= K.charAt(T);
        }
        String[] U = { "EC", "OK" };
        long[] V = new long[4];
        V[0] = x >> 24 & 255 ^ U[0].charAt(0);
        V[1] = x >> 16 & 255 ^ U[0].charAt(1);
        V[2] = x >> 8 & 255 ^ U[1].charAt(0);
        V[3] = x & 255 ^ U[1].charAt(1);
        
        long[] U1 = new long[8];
        
        for (int T = 0; T < 8; T++) {
            U1[T] = T % 2 == 0 ? N[T >> 1] : V[T >> 1];
        }
        
        String[] N1 = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F" };
        String V1 = "";
        for (long aU1 : U1) {
            V1 += N1[(int) ((aU1 >> 4) & 15)];
            V1 += N1[(int) (aU1 & 15)];
        }
        return V1;
    }
    
    @Override
    public void close() throws IOException {
        if (this.client != null) {
            this.client.dispatcher().cancelAll();
        }
    }
    
    // ---> modify
    
    public void saveAuthInfo() {
        File f = new File(workDir, "auth.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(ptwebqq);
            output.newLine();
            output.write(vfwebqq);
            output.newLine();
            output.write(String.valueOf(uin));
            output.newLine();
            output.write(psessionid);
            output.newLine();
            output.write(new Gson().toJson(cookieJar.getCookies()));
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void readAuthInfo() {
        try {
            File f = new File(workDir, "auth.txt");
            BufferedReader input = new BufferedReader(new FileReader(f));
            ptwebqq = input.readLine();
            vfwebqq = input.readLine();
            uin = Long.valueOf(input.readLine());
            psessionid = input.readLine();
            String json = input.readLine();
            cookieJar.setCookies((List<Cookie>) new Gson().fromJson(json,
                    AbstractContactHandler.type(List.class, Cookie.class)));
            input.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
