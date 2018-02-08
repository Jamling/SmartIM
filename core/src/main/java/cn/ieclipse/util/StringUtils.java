/*
 * Copyright (C) 2015-2017 QuickAF
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * StringUtils
 *
 * @author Jamling
 */
public class StringUtils {
    
    /**
     * Get request parameter value. List and Array will join by ','
     *
     * @param obj
     *            value object
     * @param charset
     *            encoding
     *
     * @return
     */
    public static String getRequestParamValue(Object obj, String charset) {
        if (obj == null) {
            return "";
        }
        String value;
        if (obj instanceof List) {
            value = StringUtils.join(",", (List<?>) obj);
        }
        else if (obj instanceof Object[]) {
            value = StringUtils.join(",", (Object[]) obj);
        }
        else {
            value = obj.toString();
        }
        try {
            return URLEncoder.encode(value, charset);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
    
    /**
     * Get request body from object
     *
     * @param input
     *            map or object
     * @param charset
     *            encoding default is utf-8
     * @param excludeNull
     *            whether exclude null default
     *            
     * @return request body string
     */
    public static String getRequestBody(Object input, String charset,
            boolean excludeNull) {
        String encode = TextUtils.isEmpty(charset) ? "UTF-8" : charset;
        if (input == null) {
            return null;
        }
        else if (input instanceof Map) {
            return StringUtils.getMapBody((Map) input, encode, excludeNull);
        }
        // TODO fix class extends same fields issue
        StringBuilder sb = new StringBuilder();
        // 获取此类所有声明的字段
        Field[] field = input.getClass().getFields();
        for (int i = 0; i < field.length && field.length > 0; i++) {
            // 值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查.
            field[i].setAccessible(true);
            // field[i].getName() 获取此字段的名称
            // field[i].get(object) 获取指定对象上此字段的值
            String name = field[i].getName();
            Object val;
            try {
                val = field[i].get(input);
            } catch (Exception e) {
                continue;
            }
            if (val == null && excludeNull) {
                continue;
            }
            sb.append(name);
            sb.append('=');
            sb.append(StringUtils.getRequestParamValue(val, encode));
            sb.append('&');
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    /**
     * @see #getRequestBody(Object, String, boolean)
     */
    public static String getMapBody(Map map, String charset,
            boolean excludeNull) {
        StringBuilder sb = new StringBuilder();
        String encode = TextUtils.isEmpty(charset) ? "UTF-8" : charset;
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            if (value == null && excludeNull) {
                continue;
            }
            sb.append(key);
            sb.append('=');
            sb.append(StringUtils.getRequestParamValue(value, encode));
            sb.append('&');
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    @Deprecated
    public static String getRequestParam(Object object, String encoding) {
        return StringUtils.getRequestBody(object, encoding, true);
    }
    
    public static String join(CharSequence delimiter, String... arrays) {
        return TextUtils.join(delimiter, arrays);
    }
    
    public static String join(CharSequence delimiter, Object[] tokens) {
        return TextUtils.join(delimiter, tokens);
    }
    
    public static String join(CharSequence delimiter, List<?> list) {
        return TextUtils.join(delimiter, list.toArray());
    }
    
    public static boolean isEmpty(CharSequence text) {
        return TextUtils.isEmpty(text);
    }
    
    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }
    
    public static String encodeXml(String src) {
        if (src == null) {
            return src;
        }
        return src.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
    
    public static String decodeXml(String src) {
        if (src == null) {
            return src;
        }
        // &apos; -> '
        // &quot; -> "
        return src.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&");
    }
    
    public static String file2url(String path) {
        if (path == null) {
            return null;
        }
        String prefix = path.startsWith("/") ? "file://" : "file:///";
        return prefix + path.replaceAll("\\\\", "/");
    }
    
    public static String file2string(Class<?> clazz, String name) {
        try {
            InputStream is = clazz.getResourceAsStream(name);
            // FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            
            return new String(buffer);
        } catch (Exception e) {
        
        }
        return null;
    }
    
    public static long getLong(String v, long dft) {
        if (StringUtils.isEmpty(v)) {
            return dft;
        }
        try {
            return Long.parseLong(v);
        } catch (Exception e) {
            return dft;
        }
    }
    
    public static int getInt(String v, int dft) {
        if (StringUtils.isEmpty(v)) {
            return dft;
        }
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            return dft;
        }
    }
    
    public static boolean getBool(String v, boolean dft) {
        if (StringUtils.isEmpty(v)) {
            return dft;
        }
        try {
            return Boolean.parseBoolean(v);
        } catch (Exception e) {
            return dft;
        }
    }
}
