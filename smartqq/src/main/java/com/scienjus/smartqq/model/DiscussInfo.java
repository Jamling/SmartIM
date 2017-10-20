package com.scienjus.smartqq.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import cn.ieclipse.smartim.model.IContact;

/**
 * 讨论组资料.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/24.
 */
public class DiscussInfo implements IContact {
    
    @SerializedName("did")
    public long id;
    
    @SerializedName("discu_name")
    public String name;
    
    public List<MemInfo> mem_list;
    
    private List<DiscussUser> users = new ArrayList<>();
    
    public void addUser(DiscussUser user) {
        this.users.add(user);
    }
    
    public String getName() {
        return name;
    }
    
    public List<DiscussUser> getUsers() {
        return users;
    }
    
    public void setUsers(List<DiscussUser> users) {
        this.users = users;
    }
    
    public DiscussUser getDiscussUser(long uin) {
        if (this.users != null) {
            for (DiscussUser u : this.users) {
                if (u.uin == uin) {
                    return u;
                }
            }
        }
        return null;
    }
    
    public long getId() {
        return id;
    }
    
    @Override
    public String getUin() {
        return String.valueOf(id);
    }
    
    public static class MemInfo implements java.io.Serializable {
        public long mem_uin;
        public long ruin;
    }
}
