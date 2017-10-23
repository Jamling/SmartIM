/*
 * Copyright 2014-2017 ieclipse.cn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ieclipse.smartim.robot;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ieclipse.util.StringUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 图灵机器人
 * 
 * @author Jamling
 * @date 2017年10月20日
 *       
 */
public class TuringRobot implements IRobot {
    
    public static final String URL = "http://www.tuling123.com/openapi/api";
    private String name;
    private String apiKey;
    private OkHttpClient client;
    
    private String userId;
    
    public TuringRobot(String name, String apiKey) {
        this.name = name;
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS).build();
    }
    
    @Override
    public String getRobotName() {
        return name;
    }
    
    @Override
    public String getRobotAnswer(String question) throws Exception {
        return getReply(null, question);
    }
    
    public String getReply(String userId, String text) throws Exception {
        JsonObject response = post(userId,
                StringUtils.getRequestParamValue(text, "utf-8"));
        Response obj = new Gson().fromJson(response, Response.class);
        return obj.text;
    }
    
    protected JsonObject post(String userId, String text) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("key", this.apiKey);
        body.addProperty("userid", userId);
        body.addProperty("info", text);
        Request request = new Request.Builder().url(URL).post(RequestBody
                .create(MediaType.parse("application/json"), body.toString()))
                .build();
        String json = client.newCall(request).execute().body().string();
        JsonObject response = new JsonParser().parse(json).getAsJsonObject();
        return response;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public static class Response implements java.io.Serializable {
        public int code;
        public String text;
    }
}
