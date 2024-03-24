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

import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.util.EncryptUtils;
import cn.ieclipse.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 图灵机器人
 * 
 * @author Jamling
 * @date 2017年10月20日
 *       
 */
public class TuringRobot extends AbsRobot {
    private static final String TURING_API_V2 = "http://openapi.tuling123.com/openapi/api/v2";
    private String apiKey;
    
    public TuringRobot(String apiKey, String extra) {
        super(0, 0);
        this.apiKey = apiKey;
    }

    private Map<String, Object> getParams(String text, IContact contact, String groupId) {
        TuringRequestV2Builder builder = new TuringRequestV2Builder(apiKey);
        builder.setText(text);
        String uid = EncryptUtils.encryptMd5(contact.getName());
        String uname = contact.getName();
        String gid = groupId == null ? null : EncryptUtils.encryptMd5(groupId);
        builder.setUserInfo(uid, uname, gid);
        // builder.setLocation(contact, contact.Province, null);
        return builder.build();
    }
    
    @Override
    public String getRobotAnswer(String question, IContact contact, String groupId)
            throws Exception {
        try {
            String body = getGson().toJson(getParams(question, contact, groupId));
            Request request = new Request.Builder().url(TURING_API_V2)
                    .post(RequestBody
                            .create(MediaType.parse("application/json"), body))
                    .build();
            String result = getClient().newCall(request).execute().body().string();
            JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
            if (obj != null && obj.has("results")) {
                JsonElement ele = obj.get("results");
                JsonObject ret = null;
                if (ele instanceof JsonObject) {
                    ret = ele.getAsJsonObject();
                }
                else if (ele instanceof JsonArray) {
                    ret = ele.getAsJsonArray().get(0).getAsJsonObject();
                }
                // System.out.println("响应json" + ret);
                if (ret != null && ret.has("values")) {
                    String type = ret.get("resultType").getAsString();
                    ret = ret.getAsJsonObject("values");
                    Response response = getGson().fromJson(ret, Response.class);
                    response.type = type;
                    if ("text".equals(type)) {
                        return response.text;
                    }
                    else if ("image".equals(type)) {
                        return response.image;
                    }
                    else if ("url".equals(type)) {
                        return response.url;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static class TuringRequestV2Builder {
        public int reqType = 0;
        public String text;
        public String apiKey;
        public String userId;
        public String groupId;
        public String userIdName;
        public String city;
        public String province;
        public String street;
        
        public TuringRequestV2Builder(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public TuringRequestV2Builder setText(String text) {
            this.text = text.length() > 128 ? text.substring(0, 128) : text;
            return this;
        }
        
        public TuringRequestV2Builder setUserInfo(String userId,
                String userName, String groupId) {
            this.userId = userId;
            this.userIdName = userName;
            this.groupId = groupId;
            return this;
        }
        
        public TuringRequestV2Builder setLocation(String city, String province,
                String street) {
            this.city = city;
            this.province = province;
            this.street = street;
            return this;
        }
        
        public Map<String, Object> build() {
            if (StringUtils.isEmpty(text)) {
                return null;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("reqType", reqType);
            
            Map<String, Object> perception = new HashMap<>();
            params.put("perception", perception);
            
            Map<String, Object> input = new HashMap<>();
            input.put("text", text);
            perception.put("inputText", input);
            
            Map<String, Object> location = new HashMap<>();
            if (!StringUtils.isEmpty(city)) {
                location.put("city", city);
            }
            if (!StringUtils.isEmpty(province)) {
                location.put("province", province);
            }
            if (!StringUtils.isEmpty(street)) {
                location.put("street", street);
            }
            if (!location.isEmpty()) {
                Map<String, Object> self = new HashMap<>();
                self.put("location", location);
                perception.put("selfInfo", self);
            }
            
            location = new HashMap<>();
            if (!StringUtils.isEmpty(userId)) {
                location.put("userId", userId);
            }
            if (!StringUtils.isEmpty(userIdName)) {
                location.put("userIdName", userIdName);
            }
            if (!StringUtils.isEmpty(groupId)) {
                location.put("groupId", groupId);
            }
            location.put("apiKey", apiKey);
            params.put("userInfo", location);
            // System.out.println(
            // String.format("图灵请求：uid=%s,name=%s,gid=%s,text=%s,city=%s",
            // userId, userIdName, groupId, text, city));
            return params;
        }
    }
    
    public static class Response implements java.io.Serializable {
        @Expose(deserialize = false, serialize = false)
        public String type;
        public String text;
        public String image;
        public String url;
    }
}
