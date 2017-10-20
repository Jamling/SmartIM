/*
 * Copyright 2014-2017 ieclipse.cn.
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

import cn.ieclipse.smartim.Utils;
import okhttp3.internal.http.HttpMethod;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月18日
 *       
 */
public final class URLConst {
    public static String BASE = "https://wx.qq.com/cgi-bin/mmwebwx-bin/";
    
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
    
    public static class Url {
        protected int method;
        protected String url;
        protected String query;
        
        public Url(String url) {
            this.url = url;
        }
        
        public String getUrl() {
            return BASE + url + getQuery();
        }
        
        public int getMethod() {
            return method;
        }
        
        protected String getQuery() {
            if (Utils.isEmpty(query)) {
                return "";
            }
            else if (url.indexOf("?") >= 0) {
                return "&" + query;
            }
            else {
                return "?" + query;
            }
        }
        
        public void setQuery(String query) {
            this.query = query;
        }
    }
    
    public static class AbsoluteUrl extends Url {
        
        public AbsoluteUrl(String url) {
            super(url);
        }
        
        @Override
        public String getUrl() {
            return url + getQuery();
        }
    }
}
