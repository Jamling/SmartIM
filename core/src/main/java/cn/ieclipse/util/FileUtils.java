package cn.ieclipse.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jamling
 */
public final class FileUtils {
    private static Logger mLogger = LoggerFactory.getLogger(FileUtils.class);
    
    private FileUtils() {
    
    }
    
    /**
     * Get file name from file path
     *
     * @param path
     *            file path
     *            
     * @return file name
     */
    public static String getName(String path) {
        File f = new File(path);
        String name = f.getName();
        return name;
    }
    
    public static String getBaseName(String path) {
        int pos = path.lastIndexOf('.');
        if (pos >= 0) {
            return path.substring(0, pos);
        }
        return path;
    }
    
    public static String getExtension(String path) {
        String name = path;// getName(path);
        int pos = name.lastIndexOf('.');
        if (pos >= 0) {
            return name.substring(pos + 1);
        }
        return "";
    }
    
    public static String getExtensionFromUrl(String url) {
        if (url != null && url.length() > 0) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }
            
            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }
            
            int filenamePos = url.lastIndexOf('/');
            String filename = 0 <= filenamePos ? url.substring(filenamePos + 1)
                    : url;
            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty() /*
                                     * && Pattern
                                     * .matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+",
                                     * filename)
                                     */) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }
        
        return "";
    }
    
    public static File getCopyDestFile(File file) {
        if (!file.exists()) {
            return file;
        }
        String name = file.getName();
        File parent = file.getParentFile();
        int pos = name.lastIndexOf('.');
        String ext = "";
        String n = name;
        if (pos >= 0) {
            ext = name.substring(pos + 1);
            n = name.substring(0, pos);
            File temp = null;
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                temp = new File(parent, n + "(" + i + ")" + "." + ext);
                if (!temp.exists()) {
                    break;
                }
            }
            return temp;
        }
        else {
            File temp = null;
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                temp = new File(parent, n + "(" + i + ")");
                if (!temp.exists()) {
                    break;
                }
            }
            return temp;
        }
    }
    
    public static boolean writeObject(File dir, String name, Object object) {
        if (dir != null && !StringUtils.isEmpty(name)) {
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    mLogger.warn("can't mkdir " + dir);
                    return false;
                }
            }
            File f = new File(dir, name);
            try {
                if (!f.exists()) {
                    if (!f.createNewFile()) {
                        mLogger.warn(
                                "can't create file " + f.getAbsolutePath());
                        return false;
                    }
                }
                ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(f));
                oos.writeObject(object);
                IOUtils.closeStream(oos);
                return true;
            } catch (Exception e) {
                mLogger.warn(
                        "an error occurred when write object to "
                                + f.getAbsolutePath() + " msg:" + e.toString(),
                        e);
                return false;
            }
        }
        return false;
    }
    
    public static Object readObject(File dir, String name) {
        Object ret = null;
        File f = new File(dir, name);
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(f));
            ret = ois.readObject();
            IOUtils.closeStream(ois);
        } catch (Exception e) {
            mLogger.warn("an error occurred when read object from "
                    + f.getAbsolutePath() + " msg:" + e.toString(), e);
        }
        return ret;
    }
    
    public static boolean mkDir(File dir, boolean create) {
        if (dir != null && !dir.exists()) {
            return dir.mkdirs();
        }
        return false;
    }
    
    public static boolean mkFile(File file, boolean create) {
        if (file != null && !file.exists()) {
            File dir = file.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            if (create) {
                try {
                    return dir.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return false;
    }
    
    public static void rmdir(File dir, boolean recursive) {
        if (dir != null && dir.exists()) {
            File[] fs = dir.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    if (f.isFile()) {
                        f.delete();
                    }
                    else {
                        if (recursive) {
                            rmdir(f, recursive);
                        }
                    }
                }
            }
            dir.delete();
        }
    }
    
    public static long getFileSize(File f) {
        if (f == null || !f.exists()) {
            return 0l;
        }
        if (f.isFile()) {
            return f.length();
        }
        else {
            long ret = 0l;
            File[] fs = f.listFiles();
            if (fs != null) {
                for (File t : fs) {
                    ret += getFileSize(t);
                }
            }
            return ret;
        }
    }
    
    public static String formatFileSize(long length) {
        if (length > (1 << 20)) {
            return length / (1 << 20) + "M";
        }
        else if (length > (1 << 10)) {
            return length / (1 << 10) + "K";
        }
        return length + "B";
    }
    
    public static void copyFileToDirectory(File srcFile, File destDir)
            throws IOException {
        if (destDir != null && srcFile != null) {
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            if (srcFile.isFile()) {
                File target = new File(destDir, srcFile.getName());
                copyFile(srcFile, target);
            }
        }
    }
    
    public static void copyDirectoryToDirectory(File srcDir, File destDir,
            FileFilter filter) throws IOException {
        if (srcDir != null && destDir != null) {
            if (srcDir.isDirectory()) {
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                File target = new File(destDir, srcDir.getName());
                if (!target.exists()) {
                    target.mkdirs();
                    target.setLastModified(srcDir.lastModified());
                }
                target.setWritable(true);
                File[] files = srcDir.listFiles(filter);
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            copyFileToDirectory(file, target);
                        }
                        else {
                            copyDirectoryToDirectory(file, target, filter);
                        }
                    }
                }
            }
        }
    }
    
    public static void copyFile(File srcFile, File destFile)
            throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        destFile.setWritable(true);
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        FileChannel in = fis.getChannel();
        FileChannel out = fos.getChannel();
        in.transferTo(0, in.size(), out);
        IOUtils.closeStream(fos);
        IOUtils.closeStream(fis);
        destFile.setLastModified(srcFile.lastModified());
    }
}
