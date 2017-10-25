package io.github.biezhi.wechat.handler;

import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.IMessage;
import io.github.biezhi.wechat.model.WechatMessage;

public class TypeMessageInterceptor implements MessageInterceptor {
    
    @Override
    public boolean handle(IMessage message) {
        if (message instanceof WechatMessage) {
            WechatMessage wxMsg = (WechatMessage) message;
            if (wxMsg.MsgType == WechatMessage.MSGTYPE_STATUSNOTIFY) {
                return true;
            }
            if (wxMsg.MsgType != WechatMessage.MSGTYPE_TEXT) {
                wxMsg.text = "暂不支持的消息，请在手机上查看";
            }
        }
        return false;
    }
    
    public void todo(WechatMessage wxMsg) {
        if (wxMsg.MsgType == WechatMessage.MSGTYPE_VIDEO) {
            wxMsg.text = "视频消息（请在手机上查看）";
        }
        else if (wxMsg.MsgType == WechatMessage.MSGTYPE_VOICE) {
            wxMsg.text = "语音消息（请在手机上查看）";
        }
        else if (wxMsg.MsgType == WechatMessage.MSGTYPE_MICROVIDEO) {
            wxMsg.text = "小视频（请在手机上查看）";
        }
        else if (wxMsg.MsgType == WechatMessage.MSGTYPE_VERIFYMSG) {
            wxMsg.text = "验证消息（请在手机上查看）";
        }
        else if (wxMsg.MsgType == WechatMessage.MSGTYPE_VOIPINVITE) {
            wxMsg.text = "视频邀请消息（请在手机上查看）";
        }
    }
}
