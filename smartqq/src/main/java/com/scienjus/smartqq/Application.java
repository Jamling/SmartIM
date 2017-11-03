package com.scienjus.smartqq;

import java.io.File;
import java.util.List;

import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.Category;
import com.scienjus.smartqq.model.DiscussFrom;
import com.scienjus.smartqq.model.Friend;
import com.scienjus.smartqq.model.GroupFrom;
import com.scienjus.smartqq.model.QQContact;

import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.callback.impl.DefaultLoginCallback;
import cn.ieclipse.smartim.model.impl.AbstractFrom;
import cn.ieclipse.smartim.model.impl.AbstractMessage;

/**
 * @author ScienJus
 * @date 2015/12/18.
 */
public class Application {
    
    public static void main(String[] args) throws Exception {
        // 创建一个新对象时需要扫描二维码登录，并且传一个处理接收到消息的回调，如果你不需要接收消息，可以传null
        final SmartQQClient client = new SmartQQClient();
        client.setWorkDir(new File("target").getAbsoluteFile());
        DefaultLoginCallback loginCallback = new DefaultLoginCallback() {
            @Override
            protected void onLoginFinish(boolean success, Exception e) {
                if (success) {
                    System.out.println("登录成功！");
                }
            }
        };
        loginCallback.setTitle("QQ", "请使用手机QQ扫描");
        client.setLoginCallback(loginCallback);
        client.login();
        client.setReceiveCallback(new ReceiveCallback() {
            
            @Override
            public void onReceiveMessage(AbstractMessage message,
                    AbstractFrom from) {
                System.out.println(from + " > " + message.getText());
                boolean unkown = false;
                QQContact qqContact = null;
                if (from instanceof GroupFrom) {
                    GroupFrom gf = (GroupFrom) from;
                    unkown = (gf.getGroupUser() == null
                            || gf.getGroupUser().isUnknown());
                    qqContact = client.getGroup(gf.getGroup().getId());
                }
                else if (from instanceof DiscussFrom) {
                    DiscussFrom gf = (DiscussFrom) from;
                    unkown = (gf.getDiscussUser() == null
                            || gf.getDiscussUser().isUnknown());
                    qqContact = client.getGroup(gf.getDiscuss().getId());
                }
                else {
                    qqContact = (Friend) (from.getContact());
                }
                System.out.println(
                        String.format("unknown?%s, newbie?%s, contact:%s",
                                unkown, from.isNewbie(), qqContact));
            }
            
            @Override
            public void onReceiveError(Throwable e) {
                e.printStackTrace(System.err);
            }
        });
        while (true) {
            if (client.isLogin()) {
                break;
            }
            Thread.sleep(1000);
        }
        
        client.init();
        // 登录成功后便可以编写你自己的业务逻辑了
        List<Category> categories = client.getFriendListWithCategory();
        for (Category category : categories) {
            System.out.println(category.getName());
            for (Friend friend : category.getFriends()) {
                System.out.println("————" + friend.getNickname());
            }
        }
        client.parseRecents(client.getRecentList());
        client.start();
        // 使用后调用close方法关闭，你也可以使用try-with-resource创建该对象并自动关闭
        // client.close();
    }
}
