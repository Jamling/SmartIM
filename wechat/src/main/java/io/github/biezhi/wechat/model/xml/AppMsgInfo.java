/*
 * Copyright 2014-2018 SmartIM.
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
package io.github.biezhi.wechat.model.xml;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2018年2月2日
 *       
 */
public class AppMsgInfo implements java.io.Serializable {
    public String appId;
    public String title;
    public String desc;
    public String url;
    public String appName;
    public int msgType;
    public int showType;
    
    @Override
    public String toString() {
        return String.format(
                "appId:%s\nappName:%s\ntitle:%s\ndesc:%s\ntype:%s\nshowType:%s\nurl:%s",
                appId, appName, title, desc, msgType, showType, url);
    }
}
