package io.github.biezhi.wechat.handler.msg;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.biezhi.wechat.model.WechatMessage;
import io.github.biezhi.wechat.model.xml.AppMsgInfo;

public class AppMsgXmlHandlerTest {
    AppMsgXmlHandler handler;
    
    @Before
    public void setUp() throws Exception {
        String content = File2String.read("appmsg-file.xml");
        WechatMessage m = new WechatMessage();
        m.Content = content;
        handler = new AppMsgXmlHandler(m);
    }
    
    @Test
    public void testGetRecents() {
        AppMsgInfo info = handler.decode();
        Assert.assertEquals("南京abc.xlsx", info.title);
        System.out.println(info);
        WechatMessage m = new WechatMessage();
        m.Content = File2String.read("appmsg-publisher.xml");
        handler = new AppMsgXmlHandler(m);
        info = handler.decode();
        Assert.assertEquals("谷歌开发者", info.appName);
        System.out.println(info);
    }
    
}
