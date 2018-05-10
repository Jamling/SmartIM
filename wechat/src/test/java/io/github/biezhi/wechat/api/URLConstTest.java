package io.github.biezhi.wechat.api;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
    
}
