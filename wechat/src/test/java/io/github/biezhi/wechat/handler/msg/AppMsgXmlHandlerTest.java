package io.github.biezhi.wechat.handler.msg;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.ieclipse.util.FileUtils;
import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.model.WechatMessage;
import io.github.biezhi.wechat.model.xml.AppMsgInfo;

public class AppMsgXmlHandlerTest {
    AppMsgXmlHandler handler;
    
    WechatMessage from(String file) {
        String content = FileUtils.readString(getClass().getResourceAsStream(file), null);
        WechatMessage m = new WechatMessage();
        m.Content = content;
        return m;
    }
    
    @Before
    public void setUp() throws Exception {
        WechatMessage m = from("appmsg-file.xml");
        handler = new AppMsgXmlHandler(m);
    }
    
    @Test
    public void testGetRecents() {
        AppMsgInfo info = handler.decode();
        Assert.assertEquals("南京abc.xlsx", info.title);
        System.out.println(info);
        WechatMessage m = from("appmsg-publisher.xml");
        handler = new AppMsgXmlHandler(m);
        info = handler.decode();
        Assert.assertEquals("谷歌开发者", info.appName);
        System.out.println(info);
    }
    
}
