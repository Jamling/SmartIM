package io.github.biezhi.wechat.handler.msg;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.biezhi.wechat.model.xml.AppMsgInfo;

public class AppMsgXmlHandlerTest {
    AppMsgXmlHandler handler;
    
    @Before
    public void setUp() throws Exception {
        String content = File2String.read("appmsg-file.xml");
        handler = new AppMsgXmlHandler(content);
    }
    
    @Test
    public void testGetRecents() {
        AppMsgInfo info = handler.decode();
        Assert.assertEquals("南京abc.xlsx", info.title);
        System.out.println(info);
        
        handler = new AppMsgXmlHandler(
                File2String.read("appmsg-publisher.xml"));
        info = handler.decode();
        Assert.assertEquals("谷歌开发者", info.appName);
        System.out.println(info);
    }
    
}
