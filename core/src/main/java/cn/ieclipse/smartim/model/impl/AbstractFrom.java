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
package cn.ieclipse.smartim.model.impl;

import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.IFrom;

/**
 * 消息来源抽象类
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public abstract class AbstractFrom implements IFrom {
    protected boolean newbie;
    protected int direction = DIR_IN;
    protected IContact target;
    
    public void setNewbie(boolean newbie) {
        this.newbie = newbie;
    }
    
    public boolean isNewbie() {
        return newbie;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    public String getName() {
        IContact contact = getMember();
        return contact == null ? "未知用户" : contact.getName();
    }
    
    @Override
    public IContact getTarget() {
        return target;
    }
    
    public void setTarget(IContact target) {
        this.target = target;
    }
    
    public boolean isOut() {
        return direction == DIR_OUT;
    }
    
    public void setOut() {
        this.direction = DIR_OUT;
    }
}
