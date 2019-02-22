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
package cn.ieclipse.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月20日
 *       
 */
public class LimitArrayList<E> extends ArrayList<E> {
    
    private int limit = 0;
    
    public LimitArrayList(int limit) {
        super();
        this.limit = limit;
    }
    
    @Override
    public boolean add(E e) {
        if (this.size() >= this.limit) {
            remove(0);
        }
        return super.add(e);
    }
    
    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c != null) {
            if (c.size() < this.limit) {
                int n = c.size() + this.size() - this.limit;
                if (n > 0) {
                    for (int i = 0; i < n; i++) {
                        remove(0);
                    }
                }
                return super.addAll(c);
            }
            else {
                clear();
                int n = c.size() - this.limit;
                Iterator<? extends E> iterator = c.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    E e = iterator.next();
                    if (i >= n) {
                        add(e);
                    }
                    i++;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }
    
    public int getLimit() {
        return limit;
    }
}
