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
package cn.ieclipse.smartim.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 有限容量的存储队列
 * 
 * @author Jamling
 * @date 2017年8月24日
 *       
 */
public class LimitQueue<E> implements Queue<E> {
    
    private int size;
    
    private Queue<E> queue = new LinkedList<E>();
    
    public LimitQueue(int size) {
        this.size = size;
    }
    
    @Override
    public boolean offer(E e) {
        if (queue.size() >= size) {
            queue.poll();
        }
        return queue.offer(e);
    }
    
    @Override
    public E poll() {
        return queue.poll();
    }
    
    @Override
    public boolean add(E e) {
        return offer(e);
    }
    
    @Override
    public E element() {
        return queue.element();
    }
    
    @Override
    public E peek() {
        return queue.peek();
    }
    
    @Override
    public boolean isEmpty() {
        return queue.size() == 0;
    }
    
    @Override
    public int size() {
        return queue.size();
    }
    
    @Override
    public E remove() {
        return queue.remove();
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c != null) {
            for (E e : c) {
                offer(e);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void clear() {
        queue.clear();
    }
    
    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }
    
    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }
    
    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }
    
    @Override
    public Object[] toArray() {
        return queue.toArray();
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }
}
