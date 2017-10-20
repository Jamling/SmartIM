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
package io.github.biezhi.wechat.handler;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.ieclipse.smartim.model.IContact;
import io.github.biezhi.wechat.model.Const;
import io.github.biezhi.wechat.model.Contact;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月10日
 *       
 */
public class WechatContactHandler extends AbstractContactHandler<Contact> {
    private int memberCount;
    private List<Contact> publicUsersList;
    private List<Contact> groupList;
    private List<Contact> specialUsersList;
    private List<Contact> memberList;
    private List<Contact> allList;
    
    @Override
    public Contact handle(JsonObject result) {
        return gson.fromJson(result, Contact.class);
    }
    
    @Override
    public List<Contact> handle(JsonArray array) {
        return gson.fromJson(array, type(List.class, Contact.class));
    }
    
    public void handleContacts(JsonObject response, IContact user) {
        this.memberCount = response.get("MemberCount").getAsInt();
        JsonArray array = response.getAsJsonArray("MemberList");
        allList = handle(array);
        this.memberList = new ArrayList<Contact>();
        this.groupList = new ArrayList<Contact>();
        this.specialUsersList = new ArrayList<Contact>();
        this.publicUsersList = new ArrayList<Contact>();
        
        if (allList != null) {
            Contact my = null;
            for (Contact contact : allList) {
                if ((contact.VerifyFlag & 8) != 0) { // 公众号/服务号
                    this.publicUsersList.add(contact);
                }
                else if (Const.API_SPECIAL_USER.contains(contact.UserName)) { // 特殊账号
                    this.specialUsersList.add(contact);
                }
                else if (contact.UserName.startsWith("@@")) {// 群聊
                    this.groupList.add(contact);
                }
                else if (contact.UserName.equals(user.getUin())) {// 自己
                    my = contact;
                }
                else {
                    this.memberList.add(contact);
                }
            }
            if (my != null) {
                allList.remove(my);
            }
        }
    }
    
    public List<Contact> handleRecents(String chatSet) {
        List<Contact> recentList = new ArrayList<Contact>();
        if (!cn.ieclipse.smartim.Utils.isEmpty(chatSet)) {
            String[] set = chatSet.split(",");
            for (int i = 0; i < set.length; i++) {
                Contact c = find(set[i], this.allList);
                if (c != null) {
                    recentList.add(c);
                }
            }
            for (Contact c : this.allList) {
                if (c.isTop()) {
                    recentList.add(0, c);
                }
            }
        }
        return recentList;
    }
    
    public List<Contact> getGroupList() {
        return groupList;
    }
    
    public int getMemberCount() {
        return memberCount;
    }
    
    public List<Contact> getMemberList() {
        return memberList;
    }
    
    public List<Contact> getPublicUsersList() {
        return publicUsersList;
    }
    
    public List<Contact> getSpecialUsersList() {
        return specialUsersList;
    }
    
    public List<Contact> getAllList() {
        return allList;
    }
    
    public Contact find(String uin, List<Contact> list) {
        if (!cn.ieclipse.smartim.Utils.isEmpty(uin)
                && !cn.ieclipse.smartim.Utils.isEmpty(list)) {
            for (Contact c : list) {
                if (c.UserName.equals(uin)) {
                    return c;
                }
            }
        }
        return null;
    }
}
