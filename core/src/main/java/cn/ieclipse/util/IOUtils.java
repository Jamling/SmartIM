/*
 * (C) Copyright 2011-2013 li.jamling@gmail.com. 
 *
 * This software is the property of li.jamling@gmail.com.
 * You have to accept the terms in the license file before use.
 *
 */
package cn.ieclipse.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An util class provided common IO operations
 * 
 * @author melord
 * @version 1.0
 */
public final class IOUtils {
    
    private static final int BUFFER_SIZE = 8192;
    
    /**
     * private constructor prevent for creating instance
     */
    private IOUtils() {
    
    }
    
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                closeable = null;
            } catch (IOException e) {
                // do nothing
            } finally {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        // do nothing
                    }
                }
            }
        }
    }
    
    /**
     * Close {@link InputStream} safely.
     * 
     * @param is
     *            input stream to be closed.
     */
    public static void closeStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // Log.v(TAG, "close inputstream meet an exception", e);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    /**
     * Close {@link OutputStream} safely.
     * 
     * @param os
     *            output stream to be closed.
     */
    public static void closeStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                // Log.v(TAG, "close inputstream meet an exception", e);
            } finally {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    /**
     * Copy data from {@link InputStream} to {@link OutputStream}.
     * 
     * @param is
     *            InputStream
     * @param os
     *            OutputStream
     * @return total bytes copied
     * @throws IOException
     *             if an I/O Exception occur
     */
    public static int copyStream(InputStream is, OutputStream os)
            throws IOException {
        int total = 0;
        int read = 0;
        byte[] buf = new byte[BUFFER_SIZE];
        while ((read = is.read(buf)) > 0) {
            os.write(buf, 0, read);
            total += read;
        }
        return total;
    }
    
    /**
     * Read data from socket input stream to a new InputStream
     * 
     * @param is
     *            socket input stream
     * @param length
     *            Content-Length of response
     * @return new InputStream
     * @throws IOException
     *             if an I/O Exception occur
     */
    public static InputStream readSocketStream(InputStream is, int length)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        copyStream(is, bos);
        byte[] data = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        closeStream(is);
        closeStream(bos);
        return bis;
    }
    
    /**
     * Read stream to byte array. Note this method not suitable for read socket
     * input stream
     * 
     * @param is
     *            InputStream
     * @return content of stream
     */
    @SuppressWarnings({ "all" })
    public static byte[] read2Byte(InputStream is) {
        int read = -1;
        int total = -1;
        byte[] data = null;
        if (is != null) {
            try {
                total = is.available();
                data = new byte[total];
                read = is.read(data);
            } catch (Exception e) {
            }
        }
        return data;
    }
}
