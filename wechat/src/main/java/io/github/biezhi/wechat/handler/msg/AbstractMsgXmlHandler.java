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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.github.biezhi.wechat.api.WechatClient;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年12月22日
 *       
 */
public class AbstractMsgXmlHandler {
    protected String content;
    protected Element root;
    protected Document document;
    
    // public Document parse(String content) throws DocumentException {
    // SAXReader reader = new SAXReader();
    // Document document = reader
    // .read(new ByteArrayInputStream(content.getBytes()));
    // return document;
    // }
    
    public AbstractMsgXmlHandler(String content) {
        this.content = content;
        try {
            this.document = DocumentHelper.parseText(content);
            this.root = this.document.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public AbstractMsgXmlHandler() {
        this.document = DocumentHelper.createDocument();
        this.root = this.document.addElement("msg");
    }
    
    public String getQueryString() {
        if (WechatClient.getInstance() != null) {
            return WechatClient.getInstance().getQueryString();
        }
        return null;
    }
}
