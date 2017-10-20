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
package com.scienjus.smartqq.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scienjus.smartqq.model.DiscussInfo;
import com.scienjus.smartqq.model.DiscussUser;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public class DiscussInfoHandler extends AbstractContactHandler<DiscussInfo> {
    
    @Override
    public DiscussInfo handle(JsonObject result) {
        DiscussInfo info = gson.fromJson(result.get("info").getAsJsonObject(),
                DiscussInfo.class);
        info.setUsers(handleUsers(result));
        return info;
    }
    
    @Override
    public List<DiscussInfo> handle(JsonArray array) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public DiscussUser handleUser(JsonObject result) {
        return gson.fromJson(result, DiscussUser.class);
    }
    
    public List<DiscussUser> handleUsers(JsonObject result) {
        Map<Long, DiscussUser> groupUserMap = new HashMap<>();
        JsonArray minfo = result.getAsJsonArray("mem_info");
        for (int i = 0; minfo != null && i < minfo.size(); i++) {
            DiscussUser groupUser = handleUser(minfo.get(i).getAsJsonObject());
            groupUserMap.put(groupUser.uin, groupUser);
        }
        JsonArray stats = result.getAsJsonArray("mem_stats");
        for (int i = 0; stats != null && i < stats.size(); i++) {
            JsonObject item = stats.get(i).getAsJsonObject();
            DiscussUser groupUser = groupUserMap
                    .get(item.get("uin").getAsLong());
            groupUser.clientType = (item.get("client_type").getAsInt());
            groupUser.status = (item.get("status").getAsString());
        }
        return new ArrayList<>(groupUserMap.values());
    }
}
