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

import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.impl.AbstractFrom;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月12日
 *       
 */
public class GroupFrom extends AbstractFrom {
    
    private Contact group;
    private Contact user;
    
    public void setGroup(Contact group) {
        this.group = group;
    }
    
    public Contact getGroup() {
        return group;
    }
    
    public void setUser(Contact user) {
        this.user = user;
    }
    
    public Contact getUser() {
        return user;
    }
    
    @Override
    public IContact getContact() {
        return getGroup();
    }
    
    @Override
    public String toString() {
        return String.format("from group(%s) %s", getContact(), getUser());
    }
}
