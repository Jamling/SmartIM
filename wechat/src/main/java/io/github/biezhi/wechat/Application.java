package io.github.biezhi.wechat;

import java.io.File;
import java.util.Date;

import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.callback.impl.DefaultLoginCallback;
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
    DefaultLoginCallback loginCallback = new DefaultLoginCallback() {
        protected void onLoginFinish(boolean success, Exception e) {
            if (success) {
                System.out.println("登录成功！");
            }
            else {
                System.err.println("登录失败!");
                e.printStackTrace();
            }
        };
    };
    
    ReceiveCallback receiveCallback = new ReceiveCallback() {
        
        @Override
        public void onReceiveMessage(AbstractMessage message,
                AbstractFrom from) {
            System.out.println(from + " " + new Date(message.getTime()));
            System.out.println(message);
        }
        
        @Override
        public void onReceiveError(Throwable e) {
            System.out.println("receive error:" + e);
        }
    };
    
    public Application() {
        WechatClient client = new WechatClient();
        client.setWorkDir(new File("target").getAbsoluteFile());
        loginCallback.setTitle("微信登录", "请使用手机微信扫码登录");
        client.setLoginCallback(loginCallback);
        client.setReceiveCallback(receiveCallback);
        client.login();
        
        while (true) {
            if (client.isLogin()) {
                try {
                    client.init();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                client.start();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
        
        Environment environment = Environment.of("classpath:config.properties");
        
        Application app = new Application();
    }
    
}