package io.github.biezhi.wechat.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ieclipse.smartim.exception.LogicException;
import cn.ieclipse.util.FileUtils;
import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.Utils;
import io.github.biezhi.wechat.model.Const;
import io.github.biezhi.wechat.model.Environment;
import io.github.biezhi.wechat.model.Session;
import io.github.biezhi.wechat.model.UploadInfo;
import io.github.biezhi.wechat.model.WechatMessage;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 微信API实现
 *
 * @author biezhi 16/06/2017
 */
public class WechatApi {
    
    private static final Logger log = LoggerFactory.getLogger(WechatApi.class);
    
    // 配置文件环境参数
    protected Environment environment;
    
    protected String appid = "wx782c26e4c19acffb";
    protected String wxHost;
    
    // 微信配置信息
    // protected Map<String, String> conf = new HashMap<String, String>();
    
    protected String wxFileHost;
    protected String redirectUri;
    
    // 登录会话
    protected Session session;
    
    protected Map<String, Object> baseRequest;
    
    protected JsonObject synckeyDic;
    protected String synckey;
    
    // device_id: 登录手机设备
    // web wechat 的格式为: e123456789012345 (e+15位随机数)
    // mobile wechat 的格式为: A1234567890abcde (A+15位随机数字或字母)
    protected String deviceId = "e" + System.currentTimeMillis();
    
    protected String userAgent = Const.API_USER_AGENT[new Random().nextInt(2)];
    protected String cookie;
    
    // 登陆账号信息
    protected JsonObject account;
    protected String chatSet;
    
    // 读取、连接、发送超时时长，单位/秒
    private int readTimeout, connTimeout, writeTimeout;
    
    private int mediaIndex = 0;
    
    public WechatApi(Environment environment, Proxy proxy) {
        this.wxHost = environment.get("wxHost", "wx.qq.com");
        this.connTimeout = environment.getInt("http.conn-time-out", 10);
        this.readTimeout = environment.getInt("http.read-time-out", 60);
        this.writeTimeout = environment.getInt("http.write-time-out", 60);
        URLConst.init(this.wxHost);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(connTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
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
     * 获取uuid
     *
     * @return
     */
    public boolean getUUID() {
        String url = URLConst.Login.uuid;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", appid);
        params.put("fun", "new");
        params.put("lang", "zh_CN");
        params.put("_", System.currentTimeMillis() + "");
        try {
            String response = doGet(url, params);
            
            String code = Utils.match("window.QRLogin.code = (\\d+);",
                    response);
            if (Utils.isBlank(code)) {
                log.warn("获取UUID失败");
                throw new LogicException(-1, "获取UUID失败，接口响应码为空");
            }
            
            if (!code.equals("200")) {
                log.warn("错误的状态码: {}", code);
                throw new LogicException(Integer.parseInt(code), "获取UUID失败");
            }
            session = new Session();
            session.setUuid(
                    Utils.match("window.QRLogin.uuid = \"(.*)\";", response));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    
    /**
     * 生成二维码
     *
     * @return 返回二维码的图片路径
     */
    public String genqrcode() {
        String url = URLConst.Login.qrcode2 + session.getUuid();
        final File output = new File(workDir, "wechat.jpg");
        if (output.getParentFile() != null
                && !output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
        }
        
        RequestBody body = new FormBody.Builder().add("t", "webwx")
                .add("_", System.currentTimeMillis() + "").build();
                
        Request request = new Request.Builder().url(url).post(body).build();
        
        try {
            Response response = client.newCall(request).execute();
            byte[] bytes = response.body().bytes();
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            log.error("[*] 生成二维码失败", e);
        }
        return output.getAbsolutePath();
    }
    
    /**
     * 等待登录
     *
     * @param tip
     *            1:等待扫描二维码 0:等待微信客户端确认
     * @return
     */
    public boolean waitforlogin(int tip) {
        Utils.sleep(tip);
        String url = URLConst.API.LOGIN + "?tip=%d&uuid=%s&_%s";
        url = String.format(url, tip, session.getUuid(),
                System.currentTimeMillis());
                
        String response = null;
        try {
            response = doGet(url, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        if (Utils.isBlank(response)) {
            log.warn("扫描二维码验证失败");
            throw new LogicException(-1, "扫描二维码验证失败");
        }
        
        String code = Utils.match("window.code=(\\d+);", response);
        if (Utils.isBlank(code)) {
            log.warn("扫描二维码验证失败");
            return false;
        }
        
        if (code.equals("201")) {
            return true;
        }
        if (code.equals("200")) {
            String pm = Utils.match("window.redirect_uri=\"(\\S+?)\";",
                    response);
            String r_uri = pm + "&fun=new";
            this.redirectUri = r_uri;
            this.wxHost = r_uri.split("://")[1].split("/")[0];
            URLConst.init(this.wxHost);
            return true;
        }
        if (code.equals("408")) {
            log.warn(Const.LOG_MSG_WAIT_LOGIN_ERR1);
        }
        else {
            log.warn(Const.LOG_MSG_WAIT_LOGIN_ERR2);
        }
        return false;
    }
    
    /**
     * 登录微信
     *
     * @return
     */
    public boolean login() throws Exception {
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(this.redirectUri);
        Request request = requestBuilder.build();
        
        log.debug("[*] 请求 => {}\n", request);
        
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        Headers headers = response.headers();
        List<String> cookies = headers.values("Set-Cookie");
        this.cookie = Utils.getCookie(cookies);
        log.info("[*] 设置cookie [{}]", this.cookie);
        if (Utils.isBlank(body)) {
            throw new LogicException(-1, "登录失败");
        }
        String error = Utils.match("<error>(\\S+)</error>", body);
        if (!StringUtils.isEmpty(error)) {
            String code = Utils.match("<ret>(\\S+)</ret>", error);
            String msg = Utils.match("<message>(\\S+)</message>", error);
            if (!"0".equals(code) && msg != null) {
                throw new LogicException(Integer.parseInt(code), msg);
            }
        }
        session.setSkey(Utils.match("<skey>(\\S+)</skey>", body));
        session.setSid(Utils.match("<wxsid>(\\S+)</wxsid>", body));
        session.setUin(Utils.match("<wxuin>(\\S+)</wxuin>", body));
        session.setPassTicket(
                Utils.match("<pass_ticket>(\\S+)</pass_ticket>", body));
                
        this.baseRequest = Utils.createMap("Uin",
                Long.valueOf(session.getUin()), "Sid", session.getSid(), "Skey",
                session.getSkey(), "DeviceID", this.deviceId);
                
        File output = new File("temp.jpg");
        if (output.exists()) {
            output.delete();
        }
        return true;
    }
    
    /**
     * 微信初始化
     *
     * @return
     * @throws WechatException
     */
    public boolean webwxinit() {
        if (null == session) {
            return false;
        }
        
        String url = URLConst.API.INIT + "?pass_ticket=%s&skey=%s&r=%s";
        url = String.format(url, session.getPassTicket(), session.getSkey(),
                System.currentTimeMillis());
                
        Map<String, Object> param = Utils.createMap("BaseRequest",
                this.baseRequest);
                
        JsonObject response;
        try {
            response = doPost(url, param).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        this.account = response.get("User").getAsJsonObject();
        this.chatSet = response.get("ChatSet").getAsString();
        this.makeSynckey(response);
        
        JsonObject baseResponse = response.getAsJsonObject("BaseResponse");
        return baseResponse.get("Ret").getAsInt() == 0;
    }
    
    private void makeSynckey(JsonObject dic) {
        this.synckeyDic = dic.getAsJsonObject("SyncKey");
        StringBuffer synckey = new StringBuffer();
        JsonArray list = this.synckeyDic.getAsJsonArray("List");
        for (JsonElement element : list) {
            JsonObject item = element.getAsJsonObject();
            synckey.append("|" + item.get("Key").getAsInt() + "_"
                    + item.get("Val").getAsInt());
        }
        this.synckey = synckey.substring(1);
    }
    
    /**
     * 开启状态通知
     *
     * @return
     */
    public boolean openStatusNotify() throws Exception {
        String url = URLConst.API.STATUS_NOTIFY + "?lang=%s&pass_ticket=%s";
        url = String.format(url, "zh_CN", session.getPassTicket());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", this.baseRequest);
        params.put("Code", 3);
        params.put("FromUserName", this.account.get("UserName").getAsString());
        params.put("ToUserName", this.account.get("UserName").getAsString());
        params.put("ClientMsgId", System.currentTimeMillis());
        
        JsonObject response = doPost(url, params).getAsJsonObject();
        if (null == response) {
            return false;
        }
        JsonObject baseResponse = response.getAsJsonObject("BaseResponse");
        return baseResponse.get("Ret").getAsInt() == 0;
    }
    
    public JsonObject wxGetContact() throws Exception {
        String url = URLConst.API.GET_CONTACT + "?pass_ticket=%s&skey=%s&r=%s";
        url = String.format(url, session.getPassTicket(), session.getSkey(),
                System.currentTimeMillis());
        JsonObject response = doPost(url, null).getAsJsonObject();
        return response;
    }
    
    /**
     * 批量获取群成员
     *
     * @param groupIds
     * @return
     */
    public JsonArray batchGetContact(List<String> groupIds) {
        String url = URLConst.API.GET_CONTACT_BATCH
                + "?type=ex&r=%s&pass_ticket=%s";
        url = String.format(url, System.currentTimeMillis(),
                session.getPassTicket());
                
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", this.baseRequest);
        params.put("Count", groupIds.size());
        
        List<Map> list = new ArrayList<Map>();
        for (String groupId : groupIds) {
            list.add(Utils.createMap("UserName", groupId, "EncryChatRoomId",
                    ""));
        }
        params.put("List", list);
        try {
            JsonElement response = doPost(url, params);
            JsonObject dic = response.getAsJsonObject();
            return dic.get("ContactList").getAsJsonArray();
        } catch (Exception e) {
            return new JsonArray();
        }
    }
    
    /**
     * 和微信保持同步
     *
     * @return
     */
    public JsonObject wxSync() throws Exception {
        String url = URLConst.API.SYNC + "?sid=%s&skey=%s&pass_ticket=%s";
        url = String.format(url, session.getSid(), session.getSkey(),
                session.getPassTicket());
                
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", this.baseRequest);
        params.put("SyncKey", this.synckeyDic);
        params.put("rr", System.currentTimeMillis());
        
        JsonElement response = doPost(url, params);
        if (null == response) {
            return null;
        }
        
        JsonObject dic = response.getAsJsonObject();
        if (null != dic) {
            JsonObject baseResponse = dic.getAsJsonObject("BaseResponse");
            if (null != baseResponse
                    && baseResponse.get("Ret").getAsInt() == 0) {
                this.makeSynckey(dic);
            }
        }
        return dic;
    }
    
    // TODO
    public boolean snapshot() {
        return false;
    }
    
    /**
     * 微信同步检查
     *
     * @return
     */
    public int[] synccheck() throws Exception {
        String url = URLConst.SYNC_CHECK;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("r", System.currentTimeMillis() + Utils.getRandomNumber(5));
        params.put("sid", session.getSid());
        params.put("uin", session.getUin());
        params.put("skey", session.getSkey());
        params.put("deviceid", this.deviceId);
        params.put("synckey", this.synckey);
        params.put("_", System.currentTimeMillis());
        
        String response = doGet(false, url, this.cookie, params);
        
        int[] arr = new int[] { -1, -1 };
        if (Utils.isBlank(response)) {
            return arr;
        }
        String retcode = Utils.match("retcode:\"(\\d+)\",", response);
        String selector = Utils.match("selector:\"(\\d+)\"}", response);
        if (null != retcode && null != selector) {
            arr[0] = Integer.parseInt(retcode);
            arr[1] = Integer.parseInt(selector);
        }
        return arr;
    }
    
    /**
     * 发送微信消息
     *
     * @param msg
     * @param to
     * @return
     * @deprecated
     */
    public JsonObject wxSendMessage(String msg, String to) throws Exception {
        
        String url = URLConst.API.SEND_MSG + "?pass_ticket=%s";
        url = String.format(url, session.getPassTicket());
        
        String clientMsgId = System.currentTimeMillis()
                + Utils.getRandomNumber(5);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", this.baseRequest);
        Map<String, Object> Msg = new HashMap<String, Object>();
        Msg.put("Type", 1);
        Msg.put("Content", Utils.unicodeToUtf8(msg));
        Msg.put("FromUserName", this.account.get("UserName").getAsString());
        Msg.put("ToUserName", to);
        Msg.put("LocalID", clientMsgId);
        Msg.put("ClientMsgId", clientMsgId);
        params.put("Msg", Msg);
        
        JsonElement response = doPost(url, params);
        if (null == response) {
            return null;
        }
        return response.getAsJsonObject();
    }
    
    public JsonObject wxSendMessage(Map<String, Object> Msg) throws Exception {
        String url = URLConst.API.SEND_MSG + "?pass_ticket=%s";
        url = String.format(url, session.getPassTicket());
        
        String clientMsgId = System.currentTimeMillis()
                + Utils.getRandomNumber(5);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", this.baseRequest);
        int type = (Integer) Msg.get("Type");
        if (type == WechatMessage.MSGTYPE_TEXT) {
        
        }
        else if (type == WechatMessage.MSGTYPE_IMAGE) {
            url = URLConst.API.SEND_IMG + "?fun=async&f=json&pass_ticket="
                    + this.session.getPassTicket();
            params.put("Scene", 0);
        }
        else if (type == WechatMessage.MSGTYPE_EMOTICON) {
            url = URLConst.API.SEND_EMOTION + "?fun=sys&f=json&pass_ticket="
                    + this.session.getPassTicket();
            params.put("Scene", 0);
            if (Msg.get("EmojiFlag") == null) {
                Msg.put("EmojiFlag", 2);
            }
        }
        else if (type == WechatMessage.MSGTYPE_FILE) {
            url = URLConst.API.SEND_FILE + "?fun=async&f=json&pass_ticket="
                    + this.session.getPassTicket();
            params.put("Scene", 0);
        }
        if (Msg.get("LocalID") == null) {
            Msg.put("LocalID", clientMsgId);
        }
        if (Msg.get("ClientMsgId") == null) {
            Msg.put("ClientMsgId", clientMsgId);
        }
        params.put("Msg", Msg);
        
        JsonElement response = doPost(url, params);
        if (null == response) {
            return null;
        }
        return response.getAsJsonObject();
    }
    
    public UploadInfo wxUploadMedia(boolean enableLog, File file, String mime,
            String media) throws Exception {
        String url = URLConst.MEDIA_UPLOAD;
        Cookie c = cookieJar.getFirstCookie("webwx_data_ticket");
        String webwx_data_ticket = "";
        if (c != null) {
            webwx_data_ticket = c.value();
        }
        
        long len = file.length();
        
        String ext = FileUtils.getExtension(file.getName()).toLowerCase();
        String mimeType = StringUtils.isEmpty(mime) ? "application/octet-stream"
                : mime;
        String mediaType = media;
        if (mediaType == null) {
            mediaType = "doc";
            if (Arrays.asList("png", "jpg", "jpeg", "bmp").contains(ext)) {
                mimeType = "image/" + ext;
                mediaType = "pic";
            }
        }
        
        Map<String, Object> req = new HashMap<String, Object>();
        req.put("BaseRequest", this.baseRequest);
        req.put("ClientMediaId",
                System.currentTimeMillis() + Utils.getRandomNumber(5));
        req.put("TotalLen", len);
        req.put("StartPos", 0);
        req.put("DataLen", len);
        req.put("MediaType", 4);
        Map<String, String> params = new HashMap<String, String>();
        params.put("f", "json");
        params.put("id", "WU_FILE_" + this.mediaIndex++);
        params.put("type", mimeType);
        params.put("lastModifiedDate",
                new Date(file.lastModified()).toString());
        params.put("size", String.valueOf(len));
        params.put("mediatype", mediaType);
        params.put("uploadmediarequest", new Gson().toJson(req));
        params.put("webwx_data_ticket", webwx_data_ticket);
        params.put("pass_ticket", session.getPassTicket());
        
        Request.Builder requestBuilder = new Request.Builder().url(url);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // if (null != cookie) {
        // builder.addHeader("Cookie", this.cookie);
        // }
        // 设置类型
        builder.setType(MultipartBody.FORM);
        // 追加参数
        for (String key : params.keySet()) {
            Object object = params.get(key);
            // if (!(object instanceof File))
            {
                builder.addFormDataPart(key, object.toString());
            }
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse(mimeType),
                file);
        builder.addFormDataPart("filename", file.getName(), fileBody);
        // 创建RequestBody
        RequestBody body = builder.build();
        Request request = requestBuilder.post(body).build();
        if (enableLog) {
            log.debug("[*] 请求 => {}\n", request);
        }
        
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        if (enableLog) {
            log.debug("[*] 响应 => {}", body);
        }
        JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
        if (obj.getAsJsonObject("BaseResponse").get("Ret").getAsInt() == 0) {
            UploadInfo info = new UploadInfo();
            info.MediaId = obj.get("MediaId").getAsString();
            try {
                info.CDNThumbImgHeight = obj.get("CDNThumbImgHeight")
                        .getAsInt();
            } catch (Exception e) {
            
            }
            try {
                info.CDNThumbImgWidth = obj.get("CDNThumbImgWidth").getAsInt();
            } catch (Exception e) {
            
            }
            info.EncryFileName = obj.get("EncryFileName").getAsString();
            return info;
        }
        return null;
    }
    
    public String wxGetIcon(String username, File file) throws Exception {
        String url = URLConst.API.GET_ICON + "?username=" + username + "&skey="
                + this.session.getSkey();
        if (file == null) {
            return url;
        }
        return doDown(false, url, this.cookie, file, null);
    }
    
    public String wxGetHead(String username, File file) throws Exception {
        String url = URLConst.API.GET_HEAD + "?username=" + username + "&skey="
                + this.session.getSkey();
        if (file == null) {
            return url;
        }
        return doDown(false, url, this.cookie, file, null);
    }
    
    public String wxGetMsgImg(String msgId, File file) throws Exception {
        String url = URLConst.API.GET_IMG + "?MsgID=" + msgId + "&skey="
                + this.session.getSkey();
        if (file == null) {
            return url;
        }
        return doDown(false, url + "&type=big", this.cookie, file, null);
    }
    
    public String wxGetMsgMedia(WechatMessage m, File file) throws Exception {
        Cookie c = cookieJar.getFirstCookie("webwx_data_ticket");
        String dt = null;
        if (c != null) {
            dt = c.value();
        }
        String skey = "";
        String pt = "";
        if (this.session != null) {
            skey = this.session.getSkey();
            pt = this.session.getPassTicket();
        }
        
        String url = String.format(
                URLConst.MEDIA_GET
                        + "?MsgID=%s&skey=%s&sender=%s&mediaid=%s&encryfilename=%s&pass_ticket=%s&webwx_data_ticket=%s",
                m.MsgId, skey, m.FromUserName, m.MediaId, m.EncryFileName, pt,
                dt);
        if (file == null) {
            return url;
        }
        Map<String, Object> params = new HashMap<>();
        // params.put("fromuser", null);
        return doDown(false, url, this.cookie, file, params);
    }
    
    /**
     * 发送文本消息
     *
     * @param msg
     * @param uid
     * @deprecated
     */
    public void sendText(String msg, String uid) throws Exception {
        this.wxSendMessage(msg, uid);
    }
    
    public void logout() {
        if (this.session == null) {
            return;
        }
        String url = URLConst.API.LOGOUT + "?redirect=0&type=1&skey="
                + this.session.getSkey();
        Map<String, Object> params = new HashMap<>();
        params.put("sid", this.session.getSid());
        params.put("uin", this.session.getUin());
        try {
            doPost(url, params);
        } catch (Exception e) {
            // ignore
        }
    }
    
    public static final MediaType JSON = MediaType
            .parse("application/json; charset=utf-8");
            
    private OkHttpClient client;
    // Cookie 管理
    private CookieManager cookieJar = new CookieManager();
    
    private String doGet(String url, Map<String, Object> params)
            throws Exception {
        return doGet(true, url, null, params);
    }
    
    private String doGet(boolean enableLog, String url, String cookie,
            Map<String, Object> params) throws Exception {
        String query = params == null ? null
                : StringUtils.getMapBody(params, null, true);
        if (!StringUtils.isEmpty(query)) {
            if (url.contains("=")) {
                url = url + "&" + query;
            }
            else {
                url = url + "?" + query;
            }
        }
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (null != cookie) {
            requestBuilder.addHeader("Cookie", this.cookie);
        }
        
        Request request = requestBuilder.build();
        if (enableLog) {
            log.debug("[*] 请求 => {}\n", request);
        }
        
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if (enableLog) {
            log.debug("[*] 响应 => {}", body);
        }
        return body;
    }
    
    private JsonElement doPost(String url, Object object) throws Exception {
        String bodyJson = null;
        RequestBody requestBody = RequestBody.create(JSON, "");
        if (null != object) {
            bodyJson = Utils.toJson(object);
            requestBody = RequestBody.create(JSON, bodyJson);
        }
        
        Request.Builder requestBuilder = new Request.Builder().url(url)
                .post(requestBody);
        if (null != cookie) {
            requestBuilder.addHeader("Cookie", cookie);
        }
        
        Request request = requestBuilder.build();
        
        log.debug("[*] 请求 => {}\n", request);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if (null != body && body.length() <= 300) {
            log.debug("[*] 响应 => {}", body);
        }
        return new JsonParser().parse(body);
        
    }
    
    private String doDown(boolean enableLog, String url, String cookie,
            File file, Map<String, Object> params) throws Exception {
        String query = params == null ? null
                : StringUtils.getMapBody(params, null, true);
        if (!StringUtils.isEmpty(query)) {
            if (url.contains("=")) {
                url = url + "&" + query;
            }
            else {
                url = url + "?" + query;
            }
        }
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (null != cookie) {
            requestBuilder.addHeader("Cookie", this.cookie);
        }
        
        Request request = requestBuilder.build();
        if (enableLog) {
            log.debug("[*] 请求 => {}\n", request);
        }
        
        Response response = client.newCall(request).execute();
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
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
            return StringUtils.file2url(file.getAbsolutePath());
        }
        return null;
    }
    
    private File workDir;
    
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }
    
    public void close() {
        cookie = null;
        if (client != null) {
            client.dispatcher().cancelAll();
        }
        logout();
    }
    
    public static class BaseRequest {
        public long Uin;
        public String Sid;
        public String Skey;
        public String DeviceID;
    }
    
    public static class BaseResponse {
        public int Ret;
        public String ErrMsg;
    }
}
