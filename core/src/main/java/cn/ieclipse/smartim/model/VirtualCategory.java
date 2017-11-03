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
package cn.ieclipse.smartim.model;

import java.util.List;

/**
 * 虚拟目录，目录中包含若干联系人
 * 
 * @author Jamling
 * @date 2017年10月14日
 *       
 * @param <T>
 *            虚拟目录下的联系人类型
 */
public class VirtualCategory<T> {
    public String name;
    public List<T> list;
    
    public VirtualCategory(String name, List<T> list) {
        this.name = name;
        this.list = list;
    }
    
    public boolean hasChildren() {
        return list != null && !list.isEmpty();
    }
    
    public Object[] getChildren() {
        return list.toArray();
    }
    
    @Override
    public String toString() {
        return name;
    }
}