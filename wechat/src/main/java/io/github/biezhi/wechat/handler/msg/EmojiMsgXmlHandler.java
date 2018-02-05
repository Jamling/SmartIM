/*
 * Copyright 2014-2017 ieclipse.cn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.biezhi.wechat.handler.msg;

import org.dom4j.Element;

import io.github.biezhi.wechat.model.WechatMessage;

/**
 * msg type = {@value WechatMessage#MSGTYPE_EMOTICON}
 * 
 * @author Jamling
 * @date 2017年12月22日
 *       
 */
public class EmojiMsgXmlHandler extends AbstractMsgXmlHandler {
    
    public EmojiMsgXmlHandler() {
        super();
    }
    
    public EmojiMsgXmlHandler(WechatMessage m) {
        super(m);
    }
    
    public String getHtml(String link) {
        // if (link != null) {
        // return String.format("<img src=\"%s\" alt=\"emoji表情\"/>", link);
        // }
        try {
            Element node = root.element("emoji");
            String type = node.attributeValue("type");
            String width = node.attributeValue("width");
            String height = node.attributeValue("height");
            String q = getQueryString();
            if (q == null) {
                q = "";
            }
            return String.format("<img src=\"%s?%s\" alt=\"emoji表情\"/>",
                    node.attributeValue("cdnurl"), q);
        } catch (Exception e) {
            return "emoji表情，请在手机上查看";
        }
    }
}
