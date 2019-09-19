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

import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.ieclipse.util.EncodeUtils;
import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.api.WechatClient;
import io.github.biezhi.wechat.model.WechatMessage;

/**
 * 处理xml消息
 * 
 * @author Jamling
 * @date 2017年12月22日
 *       
 */
public class AbstractMsgXmlHandler {
    protected String content;
    protected Element root;
    protected Document document;
    protected String rootTag = "msg";
    protected WechatMessage message;
    protected boolean unescape = true;
    protected boolean parseXmlContent = true;
    
    public String getRootTag() {
        return rootTag;
    }
    
    public boolean isUnescape() {
        return unescape;
    }
    
    public boolean isParseXmlContent() {
        return parseXmlContent;
    }
    
    public AbstractMsgXmlHandler(WechatMessage message) {
        this.message = message;
        String content = isUnescape() ? EncodeUtils.decodeXml(message.Content)
                : message.Content;
        if (isParseXmlContent() && !"该类型暂不支持，请在手机上查看".equals(content)) {
            String regex = String.format(".*(<%s>.*</%s>).*", rootTag, rootTag);
            this.content = Pattern.compile(regex, Pattern.MULTILINE)
                    .matcher(content.replaceAll("\\s*<br\\s?/>\\s*", ""))
                    .replaceAll("$1");
            try {
                this.document = DocumentHelper.parseText(this.content);
                this.root = this.document.getRootElement();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("xml content: " + content);
            }
        }
    }
    
    public AbstractMsgXmlHandler() {
        this.document = DocumentHelper.createDocument();
        this.root = this.document.addElement(rootTag);
    }
    
    public String getQueryString() {
        if (WechatClient.getInstance() != null) {
            return WechatClient.getInstance().getQueryString();
        }
        return null;
    }
}
