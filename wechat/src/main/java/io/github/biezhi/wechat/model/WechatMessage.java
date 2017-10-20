package io.github.biezhi.wechat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ieclipse.smartim.model.impl.AbstractMessage;

public class WechatMessage extends AbstractMessage {
    
    public static final int MSGTYPE_TEXT = 1;
    public static final int MSGTYPE_IMAGE = 3;
    public static final int MSGTYPE_VOICE = 34;
    public static final int MSGTYPE_VERIFYMSG = 37;
    public static final int MSGTYPE_POSSIBLEFRIEND_MSG = 40;
    public static final int MSGTYPE_SHARECARD = 42;
    public static final int MSGTYPE_VIDEO = 43;
    public static final int MSGTYPE_EMOTICON = 47;
    public static final int MSGTYPE_LOCATION = 48;
    public static final int MSGTYPE_APP = 49;
    public static final int MSGTYPE_VOIPMSG = 50;
    public static final int MSGTYPE_STATUSNOTIFY = 51;
    public static final int MSGTYPE_VOIPNOTIFY = 52;
    public static final int MSGTYPE_VOIPINVITE = 53;
    public static final int MSGTYPE_MICROVIDEO = 62;
    public static final int MSGTYPE_SYSNOTICE = 9999;
    public static final int MSGTYPE_SYS = 10000;
    public static final int MSGTYPE_RECALLED = 10002;
    
    public static final String CONTENT_DELIMITER = ":<br/>";
    
    @SerializedName("MsgType")
    public int MsgType;
    @SerializedName("FromUserName")
    public String FromUserName;
    @SerializedName("ToUserName")
    public String ToUserName;
    @SerializedName("Content")
    public String Content;
    
    @Expose(serialize = false, deserialize = false)
    public String raw;
    @SerializedName("MsgId")
    public String MsgId;
    
    @SerializedName("CreateTime")
    public long CreateTime;
    
    @Expose(serialize = false, deserialize = false)
    public String text;
    public String src;
    public String groupId;
    
    public String LocalID;
    public String ClientMsgId;
    
    @Override
    public String toString() {
        return String.format("type:%d, msg:%s", MsgType, text);
    }
}
