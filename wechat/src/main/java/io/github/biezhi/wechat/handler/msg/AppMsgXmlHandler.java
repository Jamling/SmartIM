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

import java.io.File;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.model.WechatMessage;
import io.github.biezhi.wechat.model.xml.AppMsgInfo;

/**
 * msg type = 49
 * 
 * @author Jamling
 * @date 2017年12月22日
 *       
 */
public class AppMsgXmlHandler extends AbstractMsgXmlHandler {
    
    public AppMsgXmlHandler() {
        super();
    }
    
    public AppMsgXmlHandler(String content) {
        super(content);
    }
    
    /**
     * 构造发送文件时的消息内容xml
     * 
     * @param file
     *            文件
     * @param mediaId
     *            通过文件上传返回的mediaId
     * @return 消息xml内容
     */
    public String encode(File file, String mediaId) {
        Element root = DocumentHelper.createElement("appmsg");
        root.addAttribute("appid", "wxeb7ec651dd0aefa9");
        root.addAttribute("sdkver", "");
        root.addElement("title").setText(file.getName());
        root.addElement("des");
        root.addElement("action");
        root.addElement("type").setText("6");
        root.addElement("content");
        root.addElement("url");
        root.addElement("rowurl");
        Element appattach = root.addElement("appattach");
        appattach.addElement("totallen").setText(String.valueOf(file.length()));
        appattach.addElement("attachid").setText(mediaId);
        root.addElement("extinfo");
        return root.asXML();
    }
    
    /**
     * 解析appmsg，这里只解析部分关键字段，如果解析失败，返回null
     * 
     * @return {@link AppMsgInfo}
     */
    public AppMsgInfo decode() {
        AppMsgInfo info = new AppMsgInfo();
        try {
            Element node = root.element("appmsg");
            info.appId = node.attributeValue("appid");
            info.title = node.elementTextTrim("title");
            info.desc = node.elementTextTrim("des");
            String showType = node.elementTextTrim("showtype");
            String type = node.elementTextTrim("type");
            if (!StringUtils.isEmpty(type)) {
                info.msgType = Integer.parseInt(type);
            }
            if (!StringUtils.isEmpty(showType)) {
                info.showType = Integer.parseInt(showType);
            }
            info.url = node.elementTextTrim("url");
            if (info.url != null) {
                info.url = StringUtils.decodeXml(info.url);
            }
            
            node = root.element("appinfo");
            info.appName = node.elementTextTrim("appname");
            
        } catch (Exception e) {
            return null;
        }
        return info;
    }
    
    public String getHtml(String link, WechatMessage m) {
        AppMsgInfo info = decode();
        if (info == null) {
            return this.content;
        }
        if (info.msgType == WechatMessage.MSGTYPE_FILE && link != null) {
            info.url = link;
        }
        String html = String.format("微信文件<a href=\"%s\">%s (%s)</a>", info.url,
                info.title, info.desc);
        return html;
    }
}
