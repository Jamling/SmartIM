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

import org.dom4j.Node;

import io.github.biezhi.wechat.model.WechatMessage;

/**
 * msg type = 51
 * 
 * @author Jamling
 * @date 2017年12月22日
 *       
 */
public class InitMsgXmlHandler extends AbstractMsgXmlHandler {
    
    public InitMsgXmlHandler() {
        super();
    }
    
    public InitMsgXmlHandler(WechatMessage m) {
        super(m);
    }
    
    public String getRecents() {
        try {
            Node node = root.element("op").element("username");
            return node.getText().trim();
        } catch (Exception e) {
            return null;
        }
    }
}
