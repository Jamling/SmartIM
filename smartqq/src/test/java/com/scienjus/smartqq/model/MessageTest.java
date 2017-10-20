package com.scienjus.smartqq.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Properties;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scienjus.smartqq.handler.msg.DiscussMessageHandler;
import com.scienjus.smartqq.handler.msg.GroupMessageHandler;

public class MessageTest {
    
    @Test
    public void testDiscussMessage() {
        Properties p = new Properties();
        try {
            p.load(MessageTest.class.getResourceAsStream("message.properties"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        JsonParser parser = new JsonParser();
        GroupMessageHandler handler = new GroupMessageHandler();
        DiscussMessageHandler dhHandler = new DiscussMessageHandler();
        
        JsonObject result = parser.parse(p.getProperty("g.normal"))
                .getAsJsonObject();
        GroupMessage gm = (GroupMessage) handler.handle(result);
        assertNotNull(gm.getGroupId());
        System.out.println(gm);
        
        result = parser.parse(p.getProperty("g.at")).getAsJsonObject();
        gm = (GroupMessage) handler.handle(result);
        assertTrue(gm.hasAt("157250921"));
        System.out.println(gm);
        
        result = parser.parse(p.getProperty("g.at_at")).getAsJsonObject();
        gm = (GroupMessage) handler.handle(result);
        assertTrue(gm.hasAt("157250921"));
        System.out.println(gm);
        
        result = parser.parse(p.getProperty("d.at")).getAsJsonObject();
        DiscussMessage dm = (DiscussMessage) dhHandler.handle(result);
        assertTrue(dm.hasAt("157250921"));
        System.out.println(new Date(dm.getTime() * 1000));
        System.out.println(dm.getTime());
        System.out.println(System.currentTimeMillis());
    }
    
}
