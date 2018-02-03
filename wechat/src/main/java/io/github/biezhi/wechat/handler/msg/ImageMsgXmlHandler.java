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
 * msg type = 3
 * 
 * @author Jamling
 * @date 2017年12月22日
 *       
 */
public class ImageMsgXmlHandler extends AbstractMsgXmlHandler {
    
    public ImageMsgXmlHandler() {
        super();
    }
    
    public ImageMsgXmlHandler(String content) {
        super(content);
    }
    
    public String getHtml(String link, WechatMessage m) {
        try {
            if (link == null) {
                return "对方给您发了一张图片（请在手机上查看）";
            }
            if (link != null) {
                return String.format(
                        "<img src=\"%s\" width=\"%s\" height=\"%s\" alt=\"图片\"/>",
                        link, m.ImgWidth, m.ImgHeight);
            }
            
            Element node = root.element("img");
            String q = getQueryString();
            if (q == null) {
                q = "";
            }
            String w = node.attributeValue("cdnthumbwidth");
            String h = node.attributeValue("cdnthumbheight");
            String url = node.attributeValue("cdnthumburl");
            return String.format(
                    "<img src=\"%s\" width=\"%s\" height=\"%s\" alt=\"图片\"/>",
                    link, w, h);
        } catch (Exception e) {
            return "图片消息，请在手机上查看";
        }
    }
}
