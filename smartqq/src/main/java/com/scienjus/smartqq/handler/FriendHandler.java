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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scienjus.smartqq.model.Category;
import com.scienjus.smartqq.model.Friend;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public class FriendHandler extends AbstractContactHandler<Friend> {
    
    @Override
    public Friend handle(JsonObject result) {
        return gson.fromJson(result, Friend.class);
    }
    
    @Override
    public List<Friend> handle(JsonArray array) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Map<Long, Friend> handleFriendList(JsonObject result) {
        // friend info
        Map<Long, Friend> friendMap = new TreeMap<>();
        JsonArray info = result.getAsJsonArray("info");
        for (int i = 0; info != null && i < info.size(); i++) {
            JsonObject item = info.get(i).getAsJsonObject();
            Friend friend = handle(item);
            friendMap.put(friend.getUserId(), friend);
        }
        JsonArray marknames = result.getAsJsonArray("marknames");
        for (int i = 0; marknames != null && i < marknames.size(); i++) {
            JsonObject item = marknames.get(i).getAsJsonObject();
            friendMap.get(item.get("uin").getAsLong())
                    .setMarkname(item.get("markname").getAsString());
        }
        JsonArray vipinfo = result.getAsJsonArray("vipinfo");
        for (int i = 0; vipinfo != null && i < vipinfo.size(); i++) {
            JsonObject item = vipinfo.get(i).getAsJsonObject();
            Friend friend = friendMap.get(item.get("u").getAsLong());
            friend.setVip(item.get("is_vip").getAsInt() == 1);
            friend.setVipLevel(item.get("vip_level").getAsInt());
        }
        return friendMap;
    }
    
    public Category handleCategory(JsonObject result) {
        return gson.fromJson(result, Category.class);
    }
    
    public List<Category> handleCategoryList(JsonObject result) {
        Map<Long, Friend> friendMap = handleFriendList(result);
        // category
        Map<Integer, Category> categoryMap = new TreeMap<>();
        JsonArray categories = result.getAsJsonArray("categories");
        
        categoryMap.put(0, Category.defaultCategory());
        
        for (int i = 0; categories != null && i < categories.size(); i++) {
            JsonObject item = categories.get(i).getAsJsonObject();
            Category category = handleCategory(item);
            categoryMap.put(category.getIndex(), category);
        }
        JsonArray friends = result.getAsJsonArray("friends");
        for (int i = 0; friends != null && i < friends.size(); i++) {
            JsonObject item = friends.get(i).getAsJsonObject();
            Friend friend = friendMap.get(item.get("uin").getAsLong());
            categoryMap.get(item.get("categories").getAsInt())
                    .addFriend(friend);
        }
        
        return new ArrayList<>(categoryMap.values());
    }
}
