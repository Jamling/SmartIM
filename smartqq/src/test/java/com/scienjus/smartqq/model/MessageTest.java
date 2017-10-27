package com.scienjus.smartqq.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scienjus.smartqq.handler.msg.DiscussMessageHandler;
import com.scienjus.smartqq.handler.msg.GroupMessageHandler;

public class MessageTest {
    
    Properties p;
    JsonParser parser = new JsonParser();
    GroupMessageHandler gHandler = new GroupMessageHandler();
    DiscussMessageHandler dHandler = new DiscussMessageHandler();
    
    @Before
    public void setUp() throws Exception {
        p = new Properties();
        p.load(MessageTest.class.getResourceAsStream("message.properties"));
    }
    
    @Test
    public void testGroupMessage() {
        JsonObject result = parser.parse(p.getProperty("g.normal"))
                .getAsJsonObject();
        GroupMessage gm = (GroupMessage) gHandler.handle(result);
        assertNotNull(gm.getGroupId());
        assertEquals("normal", gm.getText());
        
        result = parser.parse(p.getProperty("g.at")).getAsJsonObject();
        gm = (GroupMessage) gHandler.handle(result);
        assertTrue(gm.hasAt("157250921"));
        assertEquals("@157250921 normal", gm.getText());
        
        result = parser.parse(p.getProperty("g.at_at")).getAsJsonObject();
        gm = (GroupMessage) gHandler.handle(result);
        assertTrue(gm.hasAt("157250921"));
        assertEquals("@abc @157250921 normal", gm.getText());
        
        result = parser.parse(p.getProperty("g.at_face")).getAsJsonObject();
        gm = (GroupMessage) gHandler.handle(result);
        assertTrue(gm.hasAt("157250921"));
        assertEquals("[{\"face\":0}]@157250921 normal", gm.getText());
    }
    
    @Test
    public void testDiscussMessage() {
        String result = p.getProperty("d.at");
        DiscussMessage dm = (DiscussMessage) dHandler.handle(result);
        assertTrue(dm.hasAt("157250921"));
        assertEquals("@157250921 好", dm.getText());
        System.out.println(new Date(dm.getTime() * 1000));
        System.out.println(dm.getTime());
        System.out.println(System.currentTimeMillis());
    }
    
}
