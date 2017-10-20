package io.github.biezhi.wechat.handler;

import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.IMessage;
import io.github.biezhi.wechat.model.WechatMessage;

public class DecodeMessageInterceptor implements MessageInterceptor {
    
    @Override
    public boolean handle(IMessage message) {
        if (message instanceof WechatMessage) {
            WechatMessage wxMsg = (WechatMessage) message;
            String text = wxMsg.text;
            if (text == null) {
                text = wxMsg.Content;
            }
            wxMsg.text = text.replace("&lt;", "<").replace("&gt;", ">");
        }
        return false;
    }
    
}
