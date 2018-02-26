package com.scienjus.smartqq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.scienjus.smartqq.QNUploader.AuthInfo;
import com.scienjus.smartqq.QNUploader.UploadInfo;

public class QNUploaderTest {
    
    @Before
    public void setUp() throws Exception {
        String API_URL = "http://192.168.133.15/Think.Admin/smartqq/upload/";
        QNUploader.API_CALLBACK = API_URL + "callback";
    }
    
    @Test
    public void testGetToken() {
        QNUploader uploader = new QNUploader();
        try {
            AuthInfo auth = uploader.getToken("157250921_1", "", "", "",
                    "qrcode.png");
            System.out.println(auth);
            assertNotNull(auth);
            assertNotNull(auth.token);
            assertEquals("http://temp.ieclipse.cn", auth.domain);
            assertEquals(1, auth.days);
            assertEquals(8 << 20, auth.limit);
            
            auth = uploader.getToken("157250921_1", "ak", "sk", "",
                    "qrcode.png");
            System.out.println(auth);
            assertNotNull(auth);
            assertEquals(0, auth.days);
            assertEquals(0 << 20, auth.limit);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testUpload() {
        QNUploader uploader = new QNUploader();
        try {
            
            String ak = "";
            String sk = "";
            String bucket = "temp";
            File f = new File("pom.xml");
            Zone zone = null;
            UploadInfo info = uploader.upload("testqq", f, ak, sk, bucket,
                    zone);
            System.out.println(info);
            assertEquals("testqq/pom.xml", info.key);
            assertEquals("temp", info.bucket);
            assertEquals(f.length(), info.fsize);
            assertEquals("http://temp.ieclipse.cn/testqq/pom.xml",
                    info.getUrl("http://temp.ieclipse.cn", false));
                    
            AuthInfo auth = uploader.getToken("testqq", "", "", "",
                    "pom.xml");
            System.out.println(auth);
            assertNotNull(auth);
            assertNotNull(auth.token);
            assertEquals("http://temp.ieclipse.cn", auth.domain);
            assertEquals(1, auth.days);
            assertTrue((8 << 20) >= auth.limit);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testUpload2() {
        QNUploader uploader = new QNUploader();
        String ak = "";
        String sk = "";
        String bucket = "";
        File f = new File("C:\\Users\\Jamling\\Desktop\\a.txt");
        Zone zone = null;
        try {
            UploadInfo info = uploader.upload("157250921", f, ak, sk, bucket,
                    zone);
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testUpload3() {
    
    }
}
