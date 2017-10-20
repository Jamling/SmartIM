package com.scienjus.smartqq;

import java.io.IOException;
import java.util.List;

import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.Category;
import com.scienjus.smartqq.model.Friend;

/**
 * @author ScienJus
 * @date 2015/12/18.
 */
public class Application {
    
    public static void main(String[] args) throws Exception{
        // 创建一个新对象时需要扫描二维码登录，并且传一个处理接收到消息的回调，如果你不需要接收消息，可以传null
        final SmartQQClient client = new SmartQQClient();
        client.login();
        // 登录成功后便可以编写你自己的业务逻辑了
        List<Category> categories = client.getFriendListWithCategory();
        for (Category category : categories) {
            System.out.println(category.getName());
            for (Friend friend : category.getFriends()) {
                System.out.println("————" + friend.getNickname());
            }
        }
        // 使用后调用close方法关闭，你也可以使用try-with-resource创建该对象并自动关闭
        client.close();
    }
}
