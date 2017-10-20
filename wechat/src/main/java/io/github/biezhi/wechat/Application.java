package io.github.biezhi.wechat;

import java.awt.EventQueue;

import javax.swing.UIManager;

import cn.ieclipse.smartim.callback.LoginCallback;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.model.impl.AbstractFrom;
import cn.ieclipse.smartim.model.impl.AbstractMessage;
import io.github.biezhi.wechat.api.WechatClient;
import io.github.biezhi.wechat.model.Environment;
import io.github.biezhi.wechat.ui.QRCodeFrame;

/**
 * wechat启动程序
 */
public class Application {
    QRCodeFrame qrCodeFrame;
    LoginCallback loginCallback = new LoginCallback() {
        
        @Override
        public void onQrcode(final String path) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        if (null != qrCodeFrame)
                            qrCodeFrame.dispose();
                        UIManager.setLookAndFeel(
                                "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                        qrCodeFrame = new QRCodeFrame(path);
                    } catch (Exception e) {
                        System.err.println("显示二维码失败" + e.toString());
                    }
                }
            });
        }
        
        @Override
        public void onLogin(boolean success, Exception e) {
            if (qrCodeFrame != null) {
                qrCodeFrame.setVisible(false);
                qrCodeFrame.dispose();
            }
            if (success) {
                System.out.println("登录成功");
            }
            else {
                e.printStackTrace();
            }
        }
    };
    
    ReceiveCallback receiveCallback = new ReceiveCallback() {
        
        @Override
        public void onReceiveMessage(AbstractMessage message,
                AbstractFrom from) {
            System.out.println(from);
            System.out.println(message);
        }
        
        @Override
        public void onReceiveError(Throwable e) {
            System.out.println("receive error:" + e);
        }
    };
    
    public Application() {
        WechatClient client = new WechatClient();
        client.setLoginCallback(loginCallback);
        client.setReceiveCallback(receiveCallback);
        client.login();
        if (client.isLogin()) {
            try {
                client.init();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            client.start();
        }
    }
    
    public static void main(String[] args) throws Exception {
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
        
        Environment environment = Environment.of("classpath:config.properties");
        
        Application app = new Application();
    }
    
}