package io.github.biezhi.wechat.handler.msg;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.model.WechatMessage;

public class InitMsgXmlHandlerTest {
    InitMsgXmlHandler handler;
    
    WechatMessage from(String file) {
        String content = StringUtils.file2string(getClass(), file);
        WechatMessage m = new WechatMessage();
        m.Content = content;
        return m;
    }
    
    @Before
    public void setUp() throws Exception {
        WechatMessage m = from("init.xml");
        handler = new InitMsgXmlHandler(m);
    }
    
    @Test
    public void testGetRecents() {
        String recents = handler.getRecents();
        Assert.assertNotNull(recents);
        System.out.println(recents);
        String[] array = recents.split(",");
        System.out.println(Arrays.toString(array));
        assertEquals("filehelper", array[0]);
    }
    
}
