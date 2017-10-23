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

import com.google.gson.annotations.Expose;

import cn.ieclipse.smartim.model.IMessage;

/**
 * 抽象消息
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public abstract class AbstractMessage implements IMessage {
    /**
     * 解析之前的原始json文本
     */
    @Expose(serialize = false, deserialize = false)
    protected String raw;
    
    public String getRaw() {
        return raw;
    }
    
    public void setRaw(String raw) {
        this.raw = raw;
    }
}
