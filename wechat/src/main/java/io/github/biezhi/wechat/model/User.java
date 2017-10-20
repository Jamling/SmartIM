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
package io.github.biezhi.wechat.model;

import cn.ieclipse.smartim.Utils;
import cn.ieclipse.smartim.model.impl.AbstractContact;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月10日
 *      
 */
public class User extends AbstractContact {
    
    /**
     * Uin : xxx UserName : xxx NickName : xxx HeadImgUrl : xxx RemarkName :
     * PYInitial : PYQuanPin : RemarkPYInitial : RemarkPYQuanPin :
     * HideInputBarFlag : 0 StarFriend : 0 Sex : 1 Signature : Apt-get install B
     * AppAccountFlag : 0 VerifyFlag : 0 ContactFlag : 0 WebWxPluginSwitch : 0
     * HeadImgFlag : 1 SnsFlag : 17
     */
    
    public long Uin;
    public String UserName;
    public String NickName;
    public String HeadImgUrl;
    public String RemarkName;
    public String PYInitial;
    public String PYQuanPin;
    public String RemarkPYInitial;
    public String RemarkPYQuanPin;
    public int HideInputBarFlag;
    public int StarFriend;
    public int Sex;
    public String Signature;
    public int AppAccountFlag;
    public int VerifyFlag;
    public int ContactFlag;
    public int WebWxPluginSwitch;
    public int HeadImgFlag;
    public int SnsFlag;
    
    @Override
    public String getName() {
        if (RemarkName != null && !RemarkName.isEmpty()) {
            return RemarkName;
        }
        
        if (NickName != null && !NickName.isEmpty()) {
            return NickName;
        }
        
        return UserName;
    }
    
    @Override
    public String getUin() {
        return UserName;
    }
    
}
