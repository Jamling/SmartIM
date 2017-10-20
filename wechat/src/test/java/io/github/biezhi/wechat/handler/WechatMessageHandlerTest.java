package io.github.biezhi.wechat.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Properties;
import java.util.logging.Handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.github.biezhi.wechat.model.WechatMessage;
import junit.framework.Assert;

public class WechatMessageHandlerTest {
    Properties p = new Properties();
    WechatMessageHandler handler = new WechatMessageHandler();
    
    @Before
    public void setUp() throws Exception {
        try {
            p.load(WechatMessageHandlerTest.class
                    .getResourceAsStream("message.properties"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test() {
        String json = p.getProperty("m.init");
        WechatMessage msg = (WechatMessage) handler.handle(json);
        System.out.println(msg);
        assertEquals("@b07a26ddbe1bc6a60c2d6f5cbe4c8581", msg.FromUserName);
        json = new Gson().toJson(msg);
        System.out.println(json);
    }
    
}
