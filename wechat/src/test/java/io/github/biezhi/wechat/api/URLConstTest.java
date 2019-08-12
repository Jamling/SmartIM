package io.github.biezhi.wechat.api;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import cn.ieclipse.smartim.exception.LogicException;
import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.Utils;
import io.github.biezhi.wechat.api.URLConst.BaseApi;

public class URLConstTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void test() {
        BaseApi api = URLConst.API.INIT;
        assertEquals("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit", api.url());
        URLConst.init("wx2.qq.com");
        assertTrue(api == URLConst.API.INIT);
        assertEquals("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxinit", api.url());
    }
    
    @Test
    public void testLoginForbidden() {        
        String body = "<error><ret>1203</ret><message>为了你的帐号安全，此微信号不能登录网页微信。你可以使用Windows微信或Mac微信在电脑端登录。Windows微信下载地址：https://pc.weixin.qq.com  Mac微信下载地址：https://mac.weixin.qq.com</message></error>";
        String error = Utils.match("<error>([\\S ]+)</error>", body);
        System.out.println(error);
        if (!StringUtils.isEmpty(error)) {
            String code = Utils.match("<ret>([\\S ]+)</ret>", error);
            System.out.println(code);
            String msg = Utils.match("<message>([\\S ]+)</message>", error);
            System.out.println(msg);
            if (!"0".equals(code) && msg != null) {
                throw new LogicException(Integer.parseInt(code), msg);
            }
        }
    }
}
