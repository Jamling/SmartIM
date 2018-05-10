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
    public static String HOST = "wx.qq.com";
    
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
        public BaseApi INIT = new BaseApi("webwxinit").get();
        public BaseApi STATUS_NOTIFY = new BaseApi("webwxstatusnotify").get();
        public BaseApi SEND_MSG = new BaseApi("webwxsendmsg").get();
        public BaseApi SEND_FILE = new BaseApi("webwxsendappmsg").get();
        public BaseApi SEND_EMOTION = new BaseApi("webwxsendemoticon").get();
        public BaseApi SEND_IMG = new BaseApi("webwxsendmsgimg").get();
        public BaseApi SEND_VIDEO = new BaseApi("webwxsendmsgvideo").get();
        public BaseApi GET_ICON = new BaseApi("webwxgeticon").get();
        public BaseApi GET_HEAD = new BaseApi("webwxgetheadimg").get();
        public BaseApi GET_IMG = new BaseApi("webwxgetmsgimg").get();
        public BaseApi GET_MEDIA = new BaseApi("webwxgetmedia").get();
        public BaseApi GET_VIDEO = new BaseApi("webwxgetvideo").get();
        public BaseApi GET_VOICE = new BaseApi("webwxgetvoice").get();
        public BaseApi GET_CONTACT = new BaseApi("webwxgetcontact").get();
        public BaseApi GET_CONTACT_BATCH = new BaseApi("webwxbatchgetcontact")
                .get();
        public BaseApi LOGOUT = new BaseApi("webwxlogout").get();
        public BaseApi LOGIN = new BaseApi("login").get();
        public BaseApi SYNC = new BaseApi("webwxsync").get();
        
        public BaseApi PREVIEW = new BaseApi("webwxpreview").get();
        public BaseApi UPDATE_CHATROOM = new BaseApi("webwxupdatechatroom")
                .get();
        public BaseApi CREATE_CHATROOM = new BaseApi("webwxcreatechatroom")
                .get();
        public BaseApi CHECK_URL = new BaseApi("webwxcheckurl").get();
        public BaseApi VERIFY_USER = new BaseApi("webwxverifyuser").get();
        public BaseApi REVOKE_MSG = new BaseApi("webwxrevokemsg").get();
        public BaseApi SEARCH_CONTACT = new BaseApi("webwxsearchcontact").get();
    }
    
    public static class BaseApi {
        String path = null;
        
        BaseApi(String path) {
            this.path = path;
        }
        
        public BaseApi get() {
            return this;
        }
        
        public String url() {
            return BASE + path;
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
        
        HOST = SCHEMA + e;
        BASE = SCHEMA + e + PATH;
        SYNC_CHECK = SCHEMA + n + PATH + "synccheck";
        MEDIA_GET = SCHEMA + o + PATH + "webwxgetmedia";
        MEDIA_UPLOAD = SCHEMA + o + PATH + "webwxuploadmedia";
    }
}
