package io.github.biezhi.wechat.api;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ieclipse.smartim.exception.LogicException;
import io.github.biezhi.wechat.Utils;
import io.github.biezhi.wechat.model.Const;
import io.github.biezhi.wechat.model.Environment;
import io.github.biezhi.wechat.model.Session;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
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
    protected Map<String, String> conf = new HashMap<String, String>();
    
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
    
    public WechatApi(Environment environment) {
        this.wxHost = environment.get("wxHost", "wx.qq.com");
        this.connTimeout = environment.getInt("http.conn-time-out", 10);
        this.readTimeout = environment.getInt("http.read-time-out", 10);
        this.writeTimeout = environment.getInt("http.write-time-out", 10);
        this.conf_factory();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(connTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                }).build();
    }
    
    private void conf_factory() {
        // wx.qq.com
        String e = this.wxHost;
        String t = "login.weixin.qq.com";
        String o = "file.wx.qq.com";
        String n = "webpush.weixin.qq.com";
        
        if (e.indexOf("wx2.qq.com") > -1) {
            t = "login.wx2.qq.com";
            o = "file.wx2.qq.com";
            n = "webpush.wx2.qq.com";
        }
        else if (e.indexOf("wx8.qq.com") > -1) {
            t = "login.wx8.qq.com";
            o = "file.wx8.qq.com";
            n = "webpush.wx8.qq.com";
        }
        else if (e.indexOf("qq.com") > -1) {
            t = "login.wx.qq.com";
            o = "file.wx.qq.com";
            n = "webpush.wx.qq.com";
        }
        else if (e.indexOf("web2.wechat.com") > -1) {
            t = "login.web2.wechat.com";
            o = "file.web2.wechat.com";
            n = "webpush.web2.wechat.com";
        }
        else if (e.indexOf("wechat.com") > -1) {
            t = "login.web.wechat.com";
            o = "file.web.wechat.com";
            n = "webpush.web.wechat.com";
        }
        conf.put("LANG", "zh_CN");
        conf.put("API_jsLogin", "https://login.weixin.qq.com/jslogin");
        conf.put("API_qrcode", "https://login.weixin.qq.com/l/");
        conf.put("API_qrcode_img", "https://login.weixin.qq.com/qrcode/");
        
        conf.put("API_login", "https://" + e + "/cgi-bin/mmwebwx-bin/login");
        conf.put("API_synccheck",
                "https://" + n + "/cgi-bin/mmwebwx-bin/synccheck");
        conf.put("API_webwxdownloadmedia",
                "https://" + o + "/cgi-bin/mmwebwx-bin/webwxgetmedia");
        conf.put("API_webwxuploadmedia",
                "https://" + o + "/cgi-bin/mmwebwx-bin/webwxuploadmedia");
        conf.put("API_webwxpreview",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxpreview");
        conf.put("API_webwxinit",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxinit");
        conf.put("API_webwxgetcontact",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxgetcontact");
        conf.put("API_webwxsync",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsync");
        conf.put("API_webwxbatchgetcontact",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxbatchgetcontact");
        conf.put("API_webwxgeticon",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxgeticon");
        conf.put("API_webwxsendmsg",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsendmsg");
        conf.put("API_webwxsendmsgimg",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsendmsgimg");
        conf.put("API_webwxsendmsgvedio",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsendvideomsg");
        conf.put("API_webwxsendemoticon",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsendemoticon");
        conf.put("API_webwxsendappmsg",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsendappmsg");
        conf.put("API_webwxgetheadimg",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxgetheadimg");
        conf.put("API_webwxgetmsgimg",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxgetmsgimg");
        conf.put("API_webwxgetmedia",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxgetmedia");
        conf.put("API_webwxgetvideo",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxgetvideo");
        conf.put("API_webwxlogout",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxlogout");
        conf.put("API_webwxgetvoice",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxgetvoice");
        conf.put("API_webwxupdatechatroom",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxupdatechatroom");
        conf.put("API_webwxcreatechatroom",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxcreatechatroom");
        conf.put("API_webwxstatusnotify",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxstatusnotify");
        conf.put("API_webwxcheckurl",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxcheckurl");
        conf.put("API_webwxverifyuser",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxverifyuser");
        conf.put("API_webwxfeedback",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsendfeedback");
        conf.put("API_webwxreport",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxstatreport");
        conf.put("API_webwxsearch",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxsearchcontact");
        conf.put("API_webwxoplog",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxoplog");
        conf.put("API_checkupload",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxcheckupload");
        conf.put("API_webwxrevokemsg",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxrevokemsg");
        conf.put("API_webwxpushloginurl",
                "https://" + e + "/cgi-bin/mmwebwx-bin/webwxpushloginurl");
                
        conf.put("CONTACTFLAG_CONTACT", "1");
        conf.put("CONTACTFLAG_CHATCONTACT", "2");
        conf.put("CONTACTFLAG_CHATROOMCONTACT", "4");
        conf.put("CONTACTFLAG_BLACKLISTCONTACT", "8");
        conf.put("CONTACTFLAG_DOMAINCONTACT", "16");
        conf.put("CONTACTFLAG_HIDECONTACT", "32");
        conf.put("CONTACTFLAG_FAVOURCONTACT", "64");
        conf.put("CONTACTFLAG_3RDAPPCONTACT", "128");
        conf.put("CONTACTFLAG_SNSBLACKLISTCONTACT", "256");
        conf.put("CONTACTFLAG_NOTIFYCLOSECONTACT", "512");
        conf.put("CONTACTFLAG_TOPCONTACT", "2048");
        conf.put("MSGTYPE_TEXT", "1");
        conf.put("MSGTYPE_IMAGE", "3");
        conf.put("MSGTYPE_VOICE", "34");
        conf.put("MSGTYPE_VIDEO", "43");
        conf.put("MSGTYPE_MICROVIDEO", "62");
        conf.put("MSGTYPE_EMOTICON", "47");
        conf.put("MSGTYPE_APP", "49");
        conf.put("MSGTYPE_VOIPMSG", "50");
        conf.put("MSGTYPE_VOIPNOTIFY", "52");
        conf.put("MSGTYPE_VOIPINVITE", "53");
        conf.put("MSGTYPE_LOCATION", "48");
        conf.put("MSGTYPE_STATUSNOTIFY", "51");
        conf.put("MSGTYPE_SYSNOTICE", "9999");
        conf.put("MSGTYPE_POSSIBLEFRIEND_MSG", "40");
        conf.put("MSGTYPE_VERIFYMSG", "37");
        conf.put("MSGTYPE_SHARECARD", "42");
        conf.put("MSGTYPE_SYS", "10000");
        conf.put("MSGTYPE_RECALLED", "10002");
        conf.put("APPMSGTYPE_TEXT", "1");
        conf.put("APPMSGTYPE_IMG", "2");
        conf.put("APPMSGTYPE_AUDIO", "3");
        conf.put("APPMSGTYPE_VIDEO", "4");
        conf.put("APPMSGTYPE_URL", "5");
        conf.put("APPMSGTYPE_ATTACH", "6");
        conf.put("APPMSGTYPE_OPEN", "7");
        conf.put("APPMSGTYPE_EMOJI", "8");
        conf.put("APPMSGTYPE_VOICE_REMIND", "9");
        conf.put("APPMSGTYPE_SCAN_GOOD", "10");
        conf.put("APPMSGTYPE_GOOD", "13");
        conf.put("APPMSGTYPE_EMOTION", "15");
        conf.put("APPMSGTYPE_CARD_TICKET", "16");
        conf.put("APPMSGTYPE_REALTIME_SHARE_LOCATION", "17");
        conf.put("APPMSGTYPE_TRANSFERS", "2e3");
        conf.put("APPMSGTYPE_RED_ENVELOPES", "2001");
        conf.put("APPMSGTYPE_READER_TYPE", "100001");
        conf.put("UPLOAD_MEDIA_TYPE_IMAGE", "1");
        conf.put("UPLOAD_MEDIA_TYPE_VIDEO", "2");
        conf.put("UPLOAD_MEDIA_TYPE_AUDIO", "3");
        conf.put("UPLOAD_MEDIA_TYPE_ATTACHMENT", "4");
    }
    
    /**
     * 获取uuid
     *
     * @return
     */
    public boolean getUUID() {
        String url = conf.get("API_jsLogin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", appid);
        params.put("fun", "new");
        params.put("lang", conf.get("LANG"));
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
        String url = conf.get("API_qrcode_img") + session.getUuid();
        final File output = new File(workDir, "qrcode.jpg");
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
        String url = conf.get("API_login") + "?tip=%d&uuid=%s&_%s";
        url = String.format(url, tip, session.getUuid(),
                System.currentTimeMillis());
                
        String response = null;
        try {
            response = doGet(url);
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
            this.conf_factory();
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
    public boolean login() {
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(this.redirectUri);
        Request request = requestBuilder.build();
        
        log.debug("[*] 请求 => {}\n", request);
        try {
            Response response = client.newCall(request).execute();
            Headers headers = response.headers();
            List<String> cookies = headers.values("Set-Cookie");
            this.cookie = Utils.getCookie(cookies);
            log.info("[*] 设置cookie [{}]", this.cookie);
            String body = response.body().string();
            if (Utils.isBlank(body)) {
                return false;
            }
            session.setSkey(Utils.match("<skey>(\\S+)</skey>", body));
            session.setSid(Utils.match("<wxsid>(\\S+)</wxsid>", body));
            session.setUin(Utils.match("<wxuin>(\\S+)</wxuin>", body));
            session.setPassTicket(
                    Utils.match("<pass_ticket>(\\S+)</pass_ticket>", body));
                    
            this.baseRequest = Utils.createMap("Uin",
                    Long.valueOf(session.getUin()), "Sid", session.getSid(),
                    "Skey", session.getSkey(), "DeviceID", this.deviceId);
                    
            File output = new File("temp.jpg");
            if (output.exists()) {
                output.delete();
            }
            return true;
        } catch (Exception e) {
            log.error("[*] 登录失败", e);
            return false;
        }
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
        
        String url = conf.get("API_webwxinit") + "?pass_ticket=%s&skey=%s&r=%s";
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
        String url = conf.get("API_webwxstatusnotify")
                + "?lang=%s&pass_ticket=%s";
        url = String.format(url, conf.get("LANG"), session.getPassTicket());
        
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
        String url = conf.get("API_webwxgetcontact")
                + "?pass_ticket=%s&skey=%s&r=%s";
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
        String url = conf.get("API_webwxbatchgetcontact")
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
        String url = conf.get("API_webwxsync")
                + "?sid=%s&skey=%s&pass_ticket=%s";
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
        String url = conf.get("API_synccheck");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("r", System.currentTimeMillis() + Utils.getRandomNumber(5));
        params.put("sid", session.getSid());
        params.put("uin", session.getUin());
        params.put("skey", session.getSkey());
        params.put("deviceid", this.deviceId);
        params.put("synckey", this.synckey);
        params.put("_", System.currentTimeMillis());
        
        String response = doGet(url, this.cookie, params);
        
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
     */
    public JsonObject wxSendMessage(String msg, String to) throws Exception {
        
        String url = conf.get("API_webwxsendmsg") + "?pass_ticket=%s";
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
    
    public JsonObject wxSendMessage(int type, String body) throws Exception {
        String url = conf.get("API_webwxsendmsg") + "?pass_ticket=%s";
        url = String.format(url, session.getPassTicket());
        
        String clientMsgId = System.currentTimeMillis()
                + Utils.getRandomNumber(5);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", this.baseRequest);
        Map<String, Object> Msg = new HashMap<String, Object>();
        Msg.put("Type", type);
        params.put("Msg", body);
        
        JsonElement response = doPost(url, params);
        if (null == response) {
            return null;
        }
        return response.getAsJsonObject();
    }
    
    /**
     * 发送文本消息
     *
     * @param msg
     * @param uid
     */
    public void sendText(String msg, String uid) throws Exception {
        this.wxSendMessage(msg, uid);
    }
    
    public static final MediaType JSON = MediaType
            .parse("application/json; charset=utf-8");
            
    private OkHttpClient client;
    
    private String doGet(String url, Map<String, Object>... params)
            throws Exception {
        return doGet(url, null, params);
    }
    
    private String doGet(String url, String cookie,
            Map<String, Object>... params) throws Exception {
        if (null != params && params.length > 0) {
            Map<String, Object> param = params[0];
            Set<String> keys = param.keySet();
            StringBuilder sbuf = new StringBuilder(url);
            if (url.contains("=")) {
                sbuf.append("&");
            }
            else {
                sbuf.append("?");
            }
            for (String key : keys) {
                sbuf.append(key).append('=').append(param.get(key)).append('&');
            }
            url = sbuf.substring(0, sbuf.length() - 1);
        }
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (null != cookie) {
            requestBuilder.addHeader("Cookie", this.cookie);
        }
        
        Request request = requestBuilder.build();
        
        log.debug("[*] 请求 => {}\n", request);
        
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        
        log.debug("[*] 响应 => {}", body);
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
    
    private File workDir;
    
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
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
