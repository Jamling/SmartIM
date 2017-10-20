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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

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
    private ArrayList<Long> lineOffsets;
    private String path;
    private RandomAccessFile raf;
    private String cr = System.getProperty("line.separator");
    
    public FileStorage(int size, String path) throws IOException {
        File f = new File(path);
        // if (!f.exists()) {
        // System.out.println(f.getAbsoluteFile());
        // if (f.getAbsoluteFile().getParentFile() != null) {
        // f.getAbsoluteFile().getParentFile().mkdirs();
        // }
        // f.createNewFile();
        // }
        
        this.size = size;
        this.path = path;
        this.lineOffsets = new ArrayList<>();
        this.raf = new RandomAccessFile(path, "rw");
        this.lines = 0;
        lineOffsets.add(0l);
        String line = raf.readLine();
        while (line != null) {
            this.lines++;
            lineOffsets.add(raf.getFilePointer());
            line = raf.readLine();
        }
        raf.close();
    }
    
    public String read(int line) throws IOException {
        String ret;
        if (line < 0 || line >= lines) {
            throw new IndexOutOfBoundsException();
        }
        this.raf = new RandomAccessFile(path, "r");
        raf.seek(lineOffsets.get(line));
        ret = raf.readLine();
        raf.close();
        if (ret == null) {
            return null;
        }
        
        return new String(ret.getBytes("ISO-8859-1"), "utf-8");
    }
    
    public boolean append(String content) throws IOException {
        this.raf = new RandomAccessFile(path, "rw");
        long len1 = raf.length();
        raf.seek(len1);
        byte[] bytes = content.getBytes("utf-8");
        raf.write(bytes, 0, bytes.length);
        raf.writeBytes(cr);
        long len2 = raf.length();
        lines++;
        lineOffsets.add(len2 - len1);
        raf.close();
        raf.readLine();
        return true;
    }
    
    public int getLines() {
        return this.lines;
    }
    
    public static void main(String[] args) throws Exception {
        FileStorage fs = new FileStorage(0, "test.txt");
        System.out.println("lines:" + fs.getLines());
        System.out.println("offsets: " + fs.lineOffsets);
        fs.append("中文abc哈哈");
        String line = fs.read(fs.getLines() - 1);
        System.out.println("lines:" + line);
    }
}
