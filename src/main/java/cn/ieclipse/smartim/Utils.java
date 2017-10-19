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
package cn.ieclipse.smartim;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public abstract class Utils {
    
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
    
    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }
    
    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }
    
    public static Map<String, Object> toMap(Object obj) {
        Map<String, Object> map = new TreeMap<>();
        if (obj != null) {
            Field[] field = obj.getClass().getFields();
            for (int i = 0; i < field.length && field.length > 0; ++i) {
                field[i].setAccessible(true);
                String name = field[i].getName();
                Object val;
                try {
                    val = field[i].get(obj);
                    if (val != null) {
                        map.put(name, val);
                    }
                } catch (Exception var8) {
                    continue;
                }
            }
        }
        return map;
    }
    
    public static String formatMsg(long time, String name, String msg) {
        String s1 = new SimpleDateFormat("HH:mm:ss").format(time);
        return String.format("%s %s: %s\n", s1, name, msg);
    }
}
