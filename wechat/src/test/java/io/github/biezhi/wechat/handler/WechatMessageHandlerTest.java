package io.github.biezhi.wechat.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.github.biezhi.wechat.model.WechatMessage;

public class WechatMessageHandlerTest {
    Properties p = new Properties();
    WechatMessageHandler handler = new WechatMessageHandler();
    GroupMessageInterceptor gmInterceptor = new GroupMessageInterceptor();
    
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
        assertEquals(WechatMessage.MSGTYPE_STATUSNOTIFY, msg.MsgType);
        System.out.println(msg.getText());
        assertEquals("@b07a26ddbe1bc6a60c2d6f5cbe4c8581", msg.FromUserName);
        
        json = p.getProperty("m.g.me");
        msg = (WechatMessage) handler.handle(json);
        gmInterceptor.handle(msg);
        assertEquals(WechatMessage.MSGTYPE_TEXT, msg.MsgType);
        System.out.println(msg.getText());
        assertEquals("my msg", msg.getText());
        assertEquals("@857308baac029b0748006b3432db8444", msg.FromUserName);
        assertTrue(msg.groupId.startsWith("@@"));
        
        json = p.getProperty("m.g.you");
        msg = (WechatMessage) handler.handle(json);
        gmInterceptor.handle(msg);
        assertEquals(WechatMessage.MSGTYPE_TEXT, msg.MsgType);
        System.out.println(msg.getText());
        assertEquals("other msg", msg.getText());
        assertEquals("@88dcb5228fb45890df826b95671e770c", msg.src);
        assertTrue(msg.groupId.startsWith("@@"));
    }
    
}
