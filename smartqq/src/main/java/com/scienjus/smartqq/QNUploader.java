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
package com.scienjus.smartqq;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.scienjus.smartqq.client.CookieManager;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.File;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年8月14日
 *       
 */
public class QNUploader {
    
    public static String API_URL = "http://api.ieclipse.cn/smartqq/upload/";
    public static String API_GET_TOKEN = API_URL + "token";
    public static String API_CALLBACK = API_URL + "callback";
    
    // 客户端
    private OkHttpClient client;
    
    CookieManager cookieJar = new CookieManager();
    
    public QNUploader() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(cookieJar);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        this.client = builder.build();
    }
    
    public AuthInfo getToken(String qq, String ak, String sk, String bucket,
            String key) throws Exception {
        RequestBody body = new FormBody.Builder().add("key", key)
                .add("bucket", bucket).add("qq", qq).build();
        Request.Builder builder = new Request.Builder().url(API_GET_TOKEN)
                .addHeader("accessKey", ak).addHeader("secretKey", sk)
                .post(body);
        Request request = builder.build();
        Call call = this.client.newCall(request);
        Response response = call.execute();
        AuthResponse info = null;
        String json = response.body().string();
        System.out.println(json);
        if (response.code() == 200) {
            info = new Gson().fromJson(json, AuthResponse.class);
        }
        
        if (info == null) {
            throw new RuntimeException(response.message());
        }
        
        if (info.code != 0 || info.data == null) {
            throw new RuntimeException(info.msg);
        }
        return info.data;
    }

    public UploadInfo upload(String qq, File file, String ak, String sk,
                             String bucket) throws Exception {
        return upload(qq, file, ak, sk, bucket, null);
    }

    public UploadInfo upload(String qq, File file, String ak, String sk,
            String bucket, Zone zone) throws Exception {
            
        // 默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = String.format("%s/%s", qq, file.getName());
        AuthInfo authInfo = getToken(qq, ak, sk, bucket, key);
        System.out.println(authInfo);
        if (authInfo.limit > 0 && file.length() > authInfo.limit) {
            throw new RuntimeException("今日上传流量不足，剩余流量：" + authInfo.limit);
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(
                zone == null ? Zone.autoZone() : zone);
        // ...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        com.qiniu.http.Response response = uploadManager
                .put(file.getAbsolutePath(), key, authInfo.token);
        if (!response.isOK()) {
            throw new RuntimeException(
                    response.error + "(code=" + response.statusCode + ")");
        }
        // 解析上传成功的结果
        UploadInfo putRet = new Gson().fromJson(response.bodyString(),
                UploadInfo.class);
        if (authInfo.domain != null && !authInfo.domain.isEmpty()) {
            putRet.domain = authInfo.domain;
        }
        if (authInfo.limit > 0) {
            callback(qq, putRet);
        }
        return putRet;
    }
    
    private void callback(String qq, UploadInfo info) {
        try {
            RequestBody body = new FormBody.Builder().add("qq", qq)
                    .add("fsize", String.valueOf(info.fsize))
                    .add("key", info.key).build();
            Request.Builder builder = new Request.Builder().url(API_CALLBACK)
                    .post(body);
            Request request = builder.build();
            Call call = this.client.newCall(request);
            call.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class AuthResponse implements java.io.Serializable {
        public int code;
        public String msg;
        public AuthInfo data;
    }
    
    public static class AuthInfo implements java.io.Serializable {
        public String token;
        public String domain;
        public int days;
        public long limit;
        
        @Override
        public String toString() {
            return String.format("domain:%s, days:%d, limit:%s, token:%s",
                    domain, days, limit, token);
        }
    }
    
    public static class UploadInfo {
        public String key;
        public String hash;
        public String bucket;
        public long fsize;
        public String domain;
        
        public String getUrl() {
            return domain + "/" + key;
        }
        
        public String getUrl(String domain, boolean ts) {
            String url = this.domain;
            if (domain != null && !domain.isEmpty()) {
                url = domain;
            }
            if (url.startsWith("http://") || url.startsWith("https://")) {
            
            }
            else {
                url = "http://" + url;
            }
            if (url.endsWith("/")) {
                url += key;
            }
            else {
                url += "/" + key;
            }
            if (ts) {
                url += "?ts=" + System.currentTimeMillis();
            }
            return url;
        }
        
        @Override
        public String toString() {
            return String.format("key:%s,hash=%s,bucket=%s,fsize=%s", key, hash,
                    bucket, fsize);
        }
    }
}
