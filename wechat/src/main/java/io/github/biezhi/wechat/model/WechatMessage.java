package io.github.biezhi.wechat.model;

import java.io.File;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ieclipse.smartim.model.impl.AbstractMessage;
import cn.ieclipse.util.EncodeUtils;
import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.api.WechatClient;
import io.github.biezhi.wechat.handler.msg.AppMsgXmlHandler;
import io.github.biezhi.wechat.handler.msg.EmojiMsgXmlHandler;
import io.github.biezhi.wechat.handler.msg.ImageMsgXmlHandler;
import io.github.biezhi.wechat.model.xml.AppMsgInfo;

public class WechatMessage extends AbstractMessage {
    
    public static final int MSGTYPE_TEXT = 1;
    public static final int MSGTYPE_IMAGE = 3;
    /**
     * 慎用，并不确定
     */
    public static final int MSGTYPE_FILE = 6;
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
    
    public static final int APPMSGTYPE_TEXT = 1;
    public static final int APPMSGTYPE_IMG = 2;
    public static final int APPMSGTYPE_AUDIO = 3;
    public static final int APPMSGTYPE_VIDEO = 4;
    public static final int APPMSGTYPE_URL = 5;
    public static final int APPMSGTYPE_ATTACH = 6;
    public static final int APPMSGTYPE_OPEN = 7;
    public static final int APPMSGTYPE_EMOJI = 8;
    public static final int APPMSGTYPE_VOICE_REMIND = 9;
    public static final int APPMSGTYPE_SCAN_GOOD = 10;
    public static final int APPMSGTYPE_GOOD = 13;
    public static final int APPMSGTYPE_EMOTION = 15;
    public static final int APPMSGTYPE_CARD_TICKET = 16;
    public static final int APPMSGTYPE_REALTIME_SHARE_LOCATION = 17;
    public static final String APPMSGTYPE_TRANSFERS = "2e3";
    public static final int APPMSGTYPE_RED_ENVELOPES = 2001;
    public static final int APPMSGTYPE_READER_TYPE = 100001;
    public static final int UPLOAD_MEDIA_TYPE_IMAGE = 1;
    public static final int UPLOAD_MEDIA_TYPE_VIDEO = 2;
    public static final int UPLOAD_MEDIA_TYPE_AUDIO = 3;
    public static final int UPLOAD_MEDIA_TYPE_ATTACHMENT = 4;
    
    public static final String CONTENT_DELIMITER = ":<br/>";
    
    @SerializedName("MsgType")
    public int MsgType;
    @SerializedName("FromUserName")
    public String FromUserName;
    @SerializedName("ToUserName")
    public String ToUserName;
    @SerializedName("Content")
    public String Content;
    
    @SerializedName("MsgId")
    public String MsgId;
    
    @SerializedName("CreateTime")
    public long CreateTime;
    
    @SerializedName("StatusNotifyCode")
    public int StatusNotifyCode;
    
    @SerializedName("StatusNotifyUserName")
    public String StatusNotifyUserName;
    
    @Expose(serialize = false, deserialize = false)
    public String text;
    public String src;
    public String groupId;
    public String MediaId;
    public int AppMsgType;
    public String FileName;
    public String EncryFileName;
    public int ImgHeight;
    public int ImgWidth;
    @Expose(serialize = false, deserialize = false)
    public AppMsgInfo AppMsgInfo;
    
    @Override
    public CharSequence getText() {
        if (text == null) {
            parseContent();
        }
        return text;
    }
    
    public void parseContent() {
        if (MsgType == WechatMessage.MSGTYPE_EMOTICON) {
            text = new EmojiMsgXmlHandler(this).getHtml(getMediaLink());
        }
        else if (MsgType == WechatMessage.MSGTYPE_IMAGE) {
            text = new ImageMsgXmlHandler(this).getHtml(getMediaLink());
        }
        else if (MsgType == WechatMessage.MSGTYPE_APP) {
            text = new AppMsgXmlHandler(this).getHtml(getMediaLink());
        }
        // else if (MsgType == WechatMessage.MSGTYPE_FILE) {
        // text = new FileMsgXmlHandler(temp).getHtml(getMediaLink());
        // }
        else if (MsgType == WechatMessage.MSGTYPE_VIDEO) {
            text = "视频消息（请在手机上查看）";
        }
        else if (MsgType == WechatMessage.MSGTYPE_VOICE) {
            text = "语音消息（请在手机上查看）";
        }
        else if (MsgType == WechatMessage.MSGTYPE_MICROVIDEO) {
            text = "小视频（请在手机上查看）";
        }
        else if (MsgType == WechatMessage.MSGTYPE_VERIFYMSG) {
            text = "验证消息（请在手机上查看）";
        }
        else if (MsgType == WechatMessage.MSGTYPE_VOIPINVITE) {
            text = "视频邀请消息（请在手机上查看）";
        }
        else {
            String temp = EncodeUtils.decodeXml(Content);
            text = temp;
        }
    }
    
    public String getMediaLink() {
        if (WechatClient.getInstance() != null) {
            File dir = WechatClient.getInstance().getWorkDir("media");
            File f = null;
            if (MsgType == WechatMessage.MSGTYPE_IMAGE) {
                f = new File(dir, "image_" + MsgId + ".jpg");
            }
            else if (MsgType == WechatMessage.MSGTYPE_EMOTICON) {
                f = new File(dir, "emotion_" + MsgId + ".gif");
            }
            else if (MsgType == WechatMessage.MSGTYPE_APP) {
                if (WechatMessage.MSGTYPE_FILE == AppMsgType
                        && !StringUtils.isEmpty(FileName)) {
                    f = new File(
                            WechatClient.getInstance().getWorkDir("attach"),
                            FileName);
                }
            }
            
            String link = WechatClient.getInstance().getMediaLink(this, f);
            return link;
        }
        return null;
    }
    
    @Override
    public long getTime() {
        // fix #2 wechat timeline error refer to
        // https://github.com/Jamling/SmartQQ4IntelliJ/issues/30
        return CreateTime * 1000;
    }
    
    @Override
    public String toString() {
        return String.format("type:%d, msg:%s", MsgType, text);
    }
}
