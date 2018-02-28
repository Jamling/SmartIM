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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import cn.ieclipse.util.FileUtils;
import cn.ieclipse.util.IOUtils;

/**
 * 简单的文件存储器
 * 
 * @author Jamling
 * @date 2017年8月25日
 *       
 */
public class FileStorage {
    private static final String cr = System.getProperty("line.separator");
    private boolean persistent;
    private String path;
    private LimitArrayList<String> queue;
    
    public FileStorage(int size, String path) {
        queue = new LimitArrayList<>(size);
        this.path = path;
        BufferedReader br = null;
        try {
            File f = new File(path);
            if (f.exists()) {
                br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(f), Charset.forName("utf-8")));
                Iterator<String> itr = br.lines().iterator();
                while (itr.hasNext()) {
                    queue.add(itr.next());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                IOUtils.close(br);
            }
        }
    }
    
    public synchronized String read(int line) throws IOException {
        return queue.get(line);
    }
    
    public synchronized boolean append(String content) {
        return queue.add(content);
    }
    
    public synchronized int getLines() {
        return queue.size();
    }
    
    public synchronized List<String> getLast(int count) {
        int size = getLines();
        if (size <= count) {
            return queue;
        }
        else {
            return queue.subList(size - count, size - 1);
        }
    }
    
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
    
    public boolean isPersistent() {
        return persistent;
    }
    
    public synchronized boolean flush() {
        BufferedWriter bw;
        try {
            File f = new File(path);
            if (!f.exists()) {
                FileUtils.mkFile(f, true);
            }
            
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f), Charset.forName("utf-8")));
                    
            for (int i = 0; i < queue.size(); i++) {
                if (i > 0) {
                    bw.newLine();
                }
                String line = queue.get(i);
                bw.write(line);
                bw.flush();
            }
            IOUtils.close(bw);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public synchronized void release() {
        if (queue != null) {
            queue.clear();
        }
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }
    
    public static void main(String[] args) throws Exception {
        FileStorage fs = new FileStorage(3, "test.txt");
        fs.append("中文abc哈哈");
        String line = fs.read(0);
        System.out.println("lines:" + line);
        fs.flush();
    }
}
