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
import com.scienjus.smartqq.model.GroupInfo;
import com.scienjus.smartqq.model.GroupUser;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public class GroupInfoHandler extends AbstractContactHandler<GroupInfo> {
    
    @Override
    public GroupInfo handle(JsonObject result) {
        GroupInfo groupInfo = gson.fromJson(
                result.get("ginfo").getAsJsonObject(), GroupInfo.class);
        groupInfo.setUsers(handleUsers(result));
        return groupInfo;
    }
    
    @Override
    public List<GroupInfo> handle(JsonArray array) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public GroupUser handleUser(JsonObject result) {
        return gson.fromJson(result, GroupUser.class);
    }
    
    public List<GroupUser> handleUsers(JsonObject result) {
        Map<Long, GroupUser> groupUserMap = new HashMap<>();
        JsonArray minfo = result.getAsJsonArray("minfo");
        for (int i = 0; minfo != null && i < minfo.size(); i++) {
            GroupUser groupUser = handleUser(minfo.get(i).getAsJsonObject());
            groupUserMap.put(groupUser.uin, groupUser);
        }
        JsonArray stats = result.getAsJsonArray("stats");
        for (int i = 0; stats != null && i < stats.size(); i++) {
            JsonObject item = stats.get(i).getAsJsonObject();
            GroupUser groupUser = groupUserMap.get(item.get("uin").getAsLong());
            groupUser.setClientType(item.get("client_type").getAsInt());
            groupUser.setStatus(item.get("stat").getAsInt());
        }
        JsonArray cards = result.getAsJsonArray("cards");
        for (int i = 0; cards != null && i < cards.size(); i++) {
            JsonObject item = cards.get(i).getAsJsonObject();
            groupUserMap.get(item.get("muin").getAsLong())
                    .setCard(item.get("card").getAsString());
        }
        JsonArray vipinfo = result.getAsJsonArray("vipinfo");
        for (int i = 0; vipinfo != null && i < vipinfo.size(); i++) {
            JsonObject item = vipinfo.get(i).getAsJsonObject();
            GroupUser groupUser = groupUserMap.get(item.get("u").getAsLong());
            groupUser.setVip(item.get("is_vip").getAsInt() == 1);
            groupUser.setVipLevel(item.get("vip_level").getAsInt());
        }
        return new ArrayList<>(groupUserMap.values());
    }
}
