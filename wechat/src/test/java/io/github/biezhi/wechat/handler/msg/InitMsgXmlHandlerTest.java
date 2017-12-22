package io.github.biezhi.wechat.handler.msg;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InitMsgXmlHandlerTest {
    InitMsgXmlHandler handler;
    
    @Before
    public void setUp() throws Exception {
        String content = File2String.read("init.xml");
        handler = new InitMsgXmlHandler(content);
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
