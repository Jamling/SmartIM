/*
 * Copyright 2014-2018 SmartIM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.biezhi.wechat.api;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月18日
 *       
 */
public final class URLConst {
    public static String SCHEMA = "https://";
    public static String PATH = "/cgi-bin/mmwebwx-bin/";
    public static String BASE = SCHEMA + "wx.qq.com" + PATH;
    public static String BASE_O = "";
    public static String BASE_N = "";
    
    public interface Login {
        /**
         * post data
         * 
         * <pre>
         * appid: 应用ID wxeb7ec651dd0aefa9 
         * fun: new 应用类型 
         * lang: zh_CN 语言 
         * _: 时间戳
         * </pre>
         * 
         * return
         * 
         * <pre>
         * window.QRLogin.code = 200; window.QRLogin.uuid = "xxx"
         * 
         * </pre>
         */
        String uuid = "https://login.weixin.qq.com/jslogin";
        /**
         * get，调用前拼接上获取的UUID qrcode + {uuid} 这个是命令行下使用的，不建议使用
         * 
         * @deprecated
         */
        String qrcode = "https://login.weixin.qq.com/l/";
        
        /**
         * post
         * 
         * <pre>
         * t: webwx
         * _: time, 如1508305321009
         * </pre>
         */
        String qrcode2 = "https://login.weixin.qq.com/qrcode/";
        
        /**
         * get param
         * 
         * <pre>
         * tip: 1 未扫描 0 已扫描
         * uuid: xxx
         * _: 时间戳
         * </pre>
         * 
         * return
         * 
         * <pre>
         * window.code=xxx;
        
        xxx:
        408 登陆超时
        201 扫描成功
        200 确认登录
        
        当返回200时，还会有
        window.redirect_uri="https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=xxx&uuid=xxx&lang=xxx&scan=xxx";
         * </pre>
         */
        
        String qrlogin = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
        
        /**
         * get param:
         * 
         * <pre>
         * ticket: xxx
        uuid: xxx
        lang: zh_CN 语言
        scan: xxx
        fun: new
         * </pre>
         * 
         * return xml
         * 
         * <pre>
         * <error>
        <ret>0</ret>
        <message>OK</message>
        <skey>xxx</skey>
        <wxsid>xxx</wxsid>
        <wxuin>xxx</wxuin>
        <pass_ticket>xxx</pass_ticket>
        <isgrayscale>1</isgrayscale>
        </error>
         * </pre>
         * 
         */
        String confirm = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage";
    }
    
    public interface API {
        public String INIT = new BaseApi("webwxinit").get();
        public String STATUS_NOTIFY = new BaseApi("webwxstatusnotify").get();
        public String SEND_MSG = new BaseApi("webwxsendmsg").get();
        public String SEND_FILE = new BaseApi("webwxsendappmsg").get();
        public String SEND_EMOTION = new BaseApi("webwxsendemoticon").get();
        public String SEND_IMG = new BaseApi("webwxsendmsgimg").get();
        public String SEND_VIDEO = new BaseApi("webwxsendmsgvideo").get();
        public String GET_ICON = new BaseApi("webwxgeticon").get();
        public String GET_HEAD = new BaseApi("webwxgetheadimg").get();
        public String GET_IMG = new BaseApi("webwxgetmsgimg").get();
        public String GET_MEDIA = new BaseApi("webwxgetmedia").get();
        public String GET_VIDEO = new BaseApi("webwxgetvideo").get();
        public String GET_VOICE = new BaseApi("webwxgetvoice").get();
        public String GET_CONTACT = new BaseApi("webwxgetcontact").get();
        public String GET_CONTACT_BATCH = new BaseApi("webwxbatchgetcontact")
                .get();
        public String LOGOUT = new BaseApi("webwxlogout").get();
        public String LOGIN = new BaseApi("login").get();
        public String SYNC = new BaseApi("webwxsync").get();
        
        public String PREVIEW = new BaseApi("webwxpreview").get();
        public String UPDATE_CHATROOM = new BaseApi("webwxupdatechatroom")
                .get();
        public String CREATE_CHATROOM = new BaseApi("webwxcreatechatroom")
                .get();
        public String CHECK_URL = new BaseApi("webwxcheckurl").get();
        public String VERIFY_USER = new BaseApi("webwxverifyuser").get();
        public String REVOKE_MSG = new BaseApi("webwxrevokemsg").get();
        public String SEARCH_CONTACT = new BaseApi("webwxsearchcontact").get();
    }
    
    private static class BaseApi {
        String url = null;
        
        BaseApi(String path) {
            url = BASE + path;
        }
        
        String get() {
            return url;
        }
    }
    
    public static String SYNC_CHECK = "https://webpush.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck";
    public static String MEDIA_UPLOAD = "https://file.weixin.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia";
    public static String MEDIA_GET = "https://file.weixin.qq.com/cgi-bin/mmwebwx-bin/webwxgetmedia";
    
    public static void init(String e) {
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
        
        BASE = SCHEMA + e + PATH;
        SYNC_CHECK = SCHEMA + n + PATH + "synccheck";
        MEDIA_GET = SCHEMA + o + PATH + "webwxgetmedia";
        MEDIA_UPLOAD = SCHEMA + o + PATH + "webwxuploadmedia";
    }
}
