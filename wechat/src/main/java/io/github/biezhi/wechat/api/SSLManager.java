package io.github.biezhi.wechat.api;

import cn.ieclipse.common.EmptyTrustManager;
import okhttp3.internal.platform.Platform;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

public class SSLManager {
    private SSLManager() {
    }

    public static SSLContext newSSLContext() {
        SSLContext sslContext = Platform.get().getSSLContext();
        try {
            sslContext.init(null, new TrustManager[]{new EmptyTrustManager()}, new SecureRandom());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return sslContext;
    }

    public static X509TrustManager newX509TrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return trustManager;
    }
}
