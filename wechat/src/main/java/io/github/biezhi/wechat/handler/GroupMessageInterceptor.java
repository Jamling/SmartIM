package io.github.biezhi.wechat.handler;

import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.IMessage;
import io.github.biezhi.wechat.model.WechatMessage;

public class GroupMessageInterceptor implements MessageInterceptor {
    
    @Override
    public boolean handle(IMessage message) {
        if (message instanceof WechatMessage) {
            WechatMessage wxMsg = (WechatMessage) message;
            if (wxMsg.FromUserName.startsWith("@@")) {
                // from group msg
                if (wxMsg.Content.contains(WechatMessage.CONTENT_DELIMITER)) {
                    String[] temp = wxMsg.Content
                            .split(WechatMessage.CONTENT_DELIMITER);
                    wxMsg.src = temp[0];
                    wxMsg.text = temp[1];
                }
                wxMsg.groupId = wxMsg.FromUserName;
            }
            else if (wxMsg.ToUserName.startsWith("@@")) {
                // to group msg
                wxMsg.src = wxMsg.FromUserName;
                wxMsg.groupId = wxMsg.ToUserName;
            }
            else {
                wxMsg.src = wxMsg.FromUserName;
            }
        }
        return false;
    }
    
}
