package io.github.biezhi.wechat.handler;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.handler.MessageHandler;
import cn.ieclipse.smartim.model.IMessage;
import io.github.biezhi.wechat.model.WechatMessage;

public class WechatMessageHandler implements MessageHandler<WechatMessage> {
    protected Gson gson = new Gson();
    protected JsonParser jsonParser = new JsonParser();
    
    @Override
    public IMessage handle(JsonObject result) {
        // WechatMessage msg = new WechatMessage();
        WechatMessage msg = gson.fromJson(result, WechatMessage.class);
        msg.raw = result.toString();
        return msg;
    }
    
    @Override
    public IMessage handle(String json) {
        JsonObject result = jsonParser.parse(json).getAsJsonObject();
        return handle(result);
    }
    
    public List<WechatMessage> handleAll(JsonObject dic) {
        JsonArray msgs = dic.getAsJsonArray("AddMsgList");
        List<WechatMessage> result = new ArrayList<WechatMessage>(msgs.size());
        if (msgs.size() > 0) {
            for (JsonElement element : msgs) {
                JsonObject msg = element.getAsJsonObject();
                WechatMessage wxMsg = (WechatMessage) handle(msg);
                result.add(wxMsg);
            }
        }
        return result;
    }
}
