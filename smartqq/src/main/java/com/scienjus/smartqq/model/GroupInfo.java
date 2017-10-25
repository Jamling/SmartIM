package com.scienjus.smartqq.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import cn.ieclipse.smartim.model.IContact;

/**
 * 群资料.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/24.
 */
public class GroupInfo implements IContact {
    
    @SerializedName("gid")
    private long id;
    
    private long createtime;
    
    private String memo;
    
    private String name;
    
    private long owner;
    
    private String markname;
    
    private List<GroupUser> users = new ArrayList<>();
    
    public void addUser(GroupUser user) {
        this.users.add(user);
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getCreatetime() {
        return createtime;
    }
    
    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public long getOwner() {
        return owner;
    }
    
    public void setOwner(long owner) {
        this.owner = owner;
    }
    
    public String getMarkname() {
        return markname;
    }
    
    public void setMarkname(String markname) {
        this.markname = markname;
    }
    
    public List<GroupUser> getUsers() {
        return users;
    }
    
    public void setUsers(List<GroupUser> users) {
        this.users = users;
    }
    
    public GroupUser getGroupUser(long uin) {
        if (this.users != null) {
            for (GroupUser u : this.users) {
                if (u.uin == uin) {
                    return u;
                }
            }
        }
        return null;
    }
    
    @Override
    public String getUin() {
        return String.valueOf(getId());
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
