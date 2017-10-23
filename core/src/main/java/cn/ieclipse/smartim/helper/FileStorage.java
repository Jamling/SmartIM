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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import ch.qos.logback.core.util.FileUtil;
import cn.ieclipse.util.IOUtils;

/**
 * 简单的文件存储器
 * 
 * @author Jamling
 * @date 2017年8月25日
 *       
 */
public class FileStorage {
    private int size;
    private int lines;
    private String path;
    private String cr = System.getProperty("line.separator");
    private LimitArrayList<String> queue;
    
    public FileStorage(int size, String path) {
        queue = new LimitArrayList<>(size);
        this.path = path;
        File f = new File(path);
        if (!f.exists()) {
            FileUtil.createMissingParentDirectories(f);
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f), Charset.forName("utf-8")));
            Iterator<String> itr = br.lines().iterator();
            while (itr.hasNext()) {
                queue.add(itr.next());
            }
        } catch (Exception e) {
        
        } finally {
            if (br != null) {
                IOUtils.close(br);
            }
        }
        this.lines = queue.size();
    }
    
    public String read(int line) throws IOException {
        return queue.get(line);
    }
    
    public boolean append(String content) throws IOException {
        return queue.add(content);
    }
    
    public int getLines() {
        return queue.size();
    }
    
    public boolean flush() {
        BufferedWriter bw;
        try {
            File f = new File(path);
            if (!f.exists()) {
                FileUtil.createMissingParentDirectories(f);
                f.createNewFile();
            }
            
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f), Charset.forName("utf-8")));
                    
            for (int i = 0; i < queue.size(); i++) {
                if (i > 0) {
                    bw.newLine();
                }
                String line = queue.get(i);
                bw.write(line);
            }
            IOUtils.close(bw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void release() {
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
