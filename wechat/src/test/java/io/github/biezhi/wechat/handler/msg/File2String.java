package io.github.biezhi.wechat.handler.msg;

import java.io.InputStream;

public class File2String {
    public static String read(String name) {
        try {
            InputStream is = File2String.class.getResourceAsStream(name);
            // FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            
            return new String(buffer);
        } catch (Exception e) {
        
        }
        return null;
    }
}
