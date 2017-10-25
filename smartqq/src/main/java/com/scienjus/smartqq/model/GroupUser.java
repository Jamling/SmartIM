package com.scienjus.smartqq.model;

import cn.ieclipse.smartim.model.impl.AbstractContact;

/**
 * 群成员.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/24.
 */
public class GroupUser extends AbstractContact {
    
    public String nick;
    
    public String province;
    
    public String gender;
    
    public long uin;
    
    public String country;
    
    public String city;
    
    public String card;
    
    public int clientType;
    
    public int status;
    
    public boolean vip;
    
    public int vipLevel;
    
    public String getNick() {
        return nick;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getUin() {
        return String.valueOf(uin);
    }
    
    public void setUin(long uin) {
        this.uin = uin;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCard() {
        return card;
    }
    
    public void setCard(String card) {
        this.card = card;
    }
    
    public int getClientType() {
        return clientType;
    }
    
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
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
        if (card != null && !card.isEmpty()) {
            return card;
        }
        return nick;
    }
}
