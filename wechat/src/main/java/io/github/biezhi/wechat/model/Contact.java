package io.github.biezhi.wechat.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import cn.ieclipse.smartim.Utils;
import cn.ieclipse.smartim.model.impl.AbstractContact;

public class Contact extends AbstractContact implements Comparable<Contact> {
    
    public static final int CONTACTFLAG_CONTACT = 1;
    public static final int CONTACTFLAG_CHATCONTACT = 2;
    public static final int CONTACTFLAG_CHATROOMCONTACT = 4;
    public static final int CONTACTFLAG_BLACKLISTCONTACT = 8;
    public static final int CONTACTFLAG_DOMAINCONTACT = 16;
    public static final int CONTACTFLAG_HIDECONTACT = 32;
    public static final int CONTACTFLAG_FAVOURCONTACT = 64;
    public static final int CONTACTFLAG_3RDAPPCONTACT = 128;
    public static final int CONTACTFLAG_SNSBLACKLISTCONTACT = 256;
    public static final int CONTACTFLAG_NOTIFYCLOSECONTACT = 512;
    public static final int CONTACTFLAG_TOPCONTACT = 2048;
    
    public long Uin;
    public String UserName;
    public String NickName;
    public String HeadImgUrl;
    
    public int ContactFlag;
    public int MemberCount;
    public String RemarkName;
    public int HideInputBarFlag;
    public int Sex;
    public String Signature;
    public int VerifyFlag;
    public int OwnerUin;
    public String PYInitial;
    public String PYQuanPin;
    public String RemarkPYInitial;
    public String RemarkPYQuanPin;
    public int StarFriend;
    public int AppAccountFlag;
    public int Statues;
    public int AttrStatus;
    public String Province;
    public String City;
    public String Alias;
    public int SnsFlag;
    public int UniFriend;
    public String DisplayName;
    public int ChatRoomId;
    public String KeyWord;
    public String EncryChatRoomId;
    public List<Contact> MemberList;
    
    @Override
    public String getName() {
        if (DisplayName != null && !DisplayName.isEmpty()) {
            return DisplayName;
        }
        
        if (RemarkName != null && !RemarkName.isEmpty()) {
            return RemarkName;
        }
        
        if (NickName != null && !NickName.isEmpty()) {
            return NickName;
        }
        
        return UserName;
    }
    
    @Override
    public String getUin() {
        return UserName;
    }
    
    public Contact getMember(String uid) {
        if (!cn.ieclipse.smartim.Utils.isEmpty(this.MemberList)) {
            for (Contact t : this.MemberList) {
                if (uid != null && uid.equals(t.UserName)) {
                    return t;
                }
            }
        }
        return null;
    }
    
    @Override
    public int compareTo(Contact that) {
        int ret = 0;
        if (this.lastMessage != null) {
            if (that.lastMessage != null) {
                WechatMessage m1 = (WechatMessage) this.lastMessage;
                WechatMessage m2 = (WechatMessage) that.lastMessage;
                long diff = (m1.CreateTime - m2.CreateTime);
                ret = (int) diff;
            }
            else {
                ret = -1;
            }
        }
        else if (that.lastMessage != null) {
            ret = 1;
        }
        if (ret == 0) {
            if ((this.ContactFlag & CONTACTFLAG_TOPCONTACT) != 0) {
                ret = -1;
            }
        }
        
        return ret;
    }
    
    public boolean isTop() {
        return (this.ContactFlag & CONTACTFLAG_TOPCONTACT) != 0;
    }
    
    public String getPYInitial() {
        String py = Utils.isEmpty(RemarkPYInitial) ? PYInitial
                : RemarkPYInitial;
        return py;
    }
    
    public boolean match(String input) {
        boolean ret = false;
        if (!Utils.isEmpty(PYInitial)) {
            ret = PYInitial.toLowerCase().contains(input);
        }
        if (!ret) {
            if (!Utils.isEmpty(PYQuanPin)) {
                ret = PYQuanPin.toLowerCase().contains(input);
            }
        }
        if (!ret) {
            String name = getName().toLowerCase();
            ret = name.contains(input);
        }
        return ret;
    }
    
    @Override
    public String toString() {
        return String.format("Contact(name=%s)", getName());
    }
}
