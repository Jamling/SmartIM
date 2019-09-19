package com.scienjus.smartqq.handler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scienjus.smartqq.model.UserInfo;

import cn.ieclipse.util.FileUtils;
import cn.ieclipse.util.StringUtils;

public class UserInfoHandlerTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testHandleJsonObject() {
        String json = FileUtils.readString(getClass().getResourceAsStream(
                "get_self_info2.json"), null);
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject().getAsJsonObject("result");
        UserInfo t = new UserInfoHandler().handle(obj);
        assertEquals("157250921", t.getAccount());
    }
    
}
