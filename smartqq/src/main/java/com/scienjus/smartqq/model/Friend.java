package com.scienjus.smartqq.model;

import com.google.gson.annotations.SerializedName;

/**
 * 好友.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/18.
 */
public class Friend extends QQContact {
    
    @SerializedName("uin")
    private long userId;
    
    @SerializedName("nick")
    private String nickname;
    
    private int face;
    private long flag;
    
    private String markname = "";
    private boolean vip;
    private int vipLevel;
    
    @Override
    public String toString() {
        return getName();
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public String getMarkname() {
        return markname;
    }
    
    public void setMarkname(String markname) {
        this.markname = markname;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public boolean isVip() {
        return vip;
    }
    
    public void setVip(boolean vip) {
        this.vip = vip;
    }
    
    public int getVipLevel() {
        return vipLevel;
    }
    
    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }
    
    @Override
    public String getName() {
        if (markname != null && !markname.isEmpty()) {
            return markname;
        }
        return nickname;
    }
    
    @Override
    public String getUin() {
        return String.valueOf(getUserId());
    }
    
}
