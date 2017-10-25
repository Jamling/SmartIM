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
package com.scienjus.smartqq.handler.msg;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.Font;
import com.scienjus.smartqq.model.FriendMessage;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.QQMessage;

import cn.ieclipse.smartim.handler.MessageHandler;
import cn.ieclipse.smartim.model.IMessage;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public abstract class AbstractMessageHandler
        implements MessageHandler<QQMessage> {
        
    protected Gson gson = new Gson();
    protected JsonParser jsonParser = new JsonParser();
    
    @Override
    public IMessage handle(String json) {
        JsonObject result = jsonParser.parse(json).getAsJsonObject();
        return handle(result);
    }
    
    protected void handleCommon(QQMessage message, JsonObject result) {
        message.setRaw(result.toString());
        JsonArray cont = result.getAsJsonArray("content");
        String fontJson = cont.get(0).getAsJsonArray().get(1).toString();
        QQMessage m = (QQMessage) message;
        m.setFont(new Gson().fromJson(fontJson, Font.class));
        m.setTime(result.get("time").getAsLong());
        
        final int size = cont.size();
        final StringBuilder contentBuilder = new StringBuilder();
        int start = 1;
        // if (size > 1 && cont.get(1) instanceof JsonArray) {
        // start++;
        // }
        
        for (int i = start; i < size; i++) {
            JsonElement e = cont.get(i);
            if (e instanceof JsonPrimitive && e.getAsString().startsWith("@")) {
                m.addAt(e.getAsString());
            }
            contentBuilder.append(e.getAsString());
        }
        m.setContent(contentBuilder.toString());
        m.setRaw(result.toString());
        
        if (m instanceof DiscussMessage) {
            DiscussMessage dm = (DiscussMessage) m;
            dm.setDiscussId(result.get("did").getAsLong());
            dm.setUserId(result.get("send_uin").getAsLong());
        }
        else if (m instanceof GroupMessage) {
            GroupMessage gm = (GroupMessage) m;
            gm.setGroupId(result.get("group_code").getAsLong());
            gm.setUserId(result.get("send_uin").getAsLong());
        }
        else if (m instanceof FriendMessage) {
            m.setUserId(result.get("from_uin").getAsLong());
        }
    }
    
    /**
     * TODO 将自己发送的消息序列化
     * 
     * @param message
     * @return
     */
    protected JsonObject serializeCommon(QQMessage message) {
        JsonObject result = new JsonObject();
        return result;
    }
}
