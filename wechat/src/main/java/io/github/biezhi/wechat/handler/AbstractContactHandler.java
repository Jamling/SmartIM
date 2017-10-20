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
package io.github.biezhi.wechat.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.Gson;

import cn.ieclipse.smartim.handler.ContactHandler;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public abstract class AbstractContactHandler<E> implements ContactHandler<E> {
    protected Gson gson = new Gson();
    
    public static ParameterizedType type(final Type raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }
            
            public Type[] getActualTypeArguments() {
                return args;
            }
            
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
