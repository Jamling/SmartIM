package io.github.biezhi.wechat.api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.ieclipse.smartim.AbstractSmartClient;
import cn.ieclipse.smartim.callback.LoginCallback;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.exception.LogicException;
import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.IMessage;
import cn.ieclipse.smartim.model.impl.AbstractFrom;
import cn.ieclipse.smartim.model.impl.AbstractMessage;
import io.github.biezhi.wechat.Utils;
import io.github.biezhi.wechat.handler.DecodeMessageInterceptor;
import io.github.biezhi.wechat.handler.GroupMessageInterceptor;
import io.github.biezhi.wechat.handler.TypeMessageInterceptor;
import io.github.biezhi.wechat.handler.WechatContactHandler;
import io.github.biezhi.wechat.handler.WechatMessageHandler;
import io.github.biezhi.wechat.model.Const;
import io.github.biezhi.wechat.model.Contact;
import io.github.biezhi.wechat.model.Environment;
import io.github.biezhi.wechat.model.GroupFrom;
import io.github.biezhi.wechat.model.UserFrom;
import io.github.biezhi.wechat.model.WechatMessage;

/**
 * Created by Jamling on 2017/7/17.
 */
public class WechatClient extends AbstractSmartClient {
    private static final Logger log = LoggerFactory
            .getLogger(WechatClient.class);
            
    private WechatApi api;
    private int memberCount;
    private List<Contact> publicUsersList;
    private List<Contact> groupList;
    private List<Contact> specialUsersList;
    private List<Contact> memberList;
    private List<Contact> recentList;
    private List<Contact> allList;
    private Contact accout;
    
    private WechatContactHandler contactHandler = new WechatContactHandler();
    
    public WechatClient() {
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
        Environment environment = Environment.of("classpath:config.properties");
        api = new WechatApi(environment);
        addMessageInterceptor(new TypeMessageInterceptor());
        addMessageInterceptor(new GroupMessageInterceptor());
        addMessageInterceptor(new DecodeMessageInterceptor());
    }
    
    private boolean waitForLogin() {
        int maxCount = 30;
        while (maxCount > 0) {
            Utils.sleep(maxCount < 15 ? 3 : 1);
            log.info(Const.LOG_MSG_SCAN_QRCODE);
            if (!api.waitforlogin(1)) {
                maxCount--;
                continue;
            }
            log.info(Const.LOG_MSG_CONFIRM_LOGIN);
            if (!api.waitforlogin(0)) {
                maxCount--;
                continue;
            }
            return true;
        }
        return false;
    }
    
    public void login() {
        isLogin = false;
        try {
            log.info(Const.LOG_MSG_GET_UUID);
            api.getUUID();
            log.info(Const.LOG_MSG_GET_QRCODE);
            final String qrCodePath = api.genqrcode();
            if (loginCallback != null) {
                loginCallback.onQrcode(qrCodePath);
            }
            if (waitForLogin() && api.login() && api.webwxinit()) {
                isLogin = true;
                accout = contactHandler.handle(api.account);
                if (loginCallback != null) {
                    loginCallback.onLogin(true, null);
                }
                return;
            }
        } catch (Exception e) {
            loginCallback.onLogin(false, e);
        }
    }
    
    public void close() {
        pollStarted = false;
        isClose = true;
        isLogin = false;
        
    }
    
    @Override
    public void setWorkDir(File path) {
        api.setWorkDir(path);
    }
    
    @Override
    public void init() throws Exception {
        log.info(Const.LOG_MSG_STATUS_NOTIFY);
        if (!api.openStatusNotify()) {
            log.warn("状态通知打开失败");
        }
        log.info(Const.LOG_MSG_GET_CONTACT);
        JsonObject response = api.wxGetContact();
        if (response == null) {
            log.warn("获取联系人失败");
            return;
        }
        contactHandler.handleContacts(response, getAccount());
        this.groupList = contactHandler.getGroupList();
        this.memberList = contactHandler.getMemberList();
        this.publicUsersList = contactHandler.getPublicUsersList();
        this.specialUsersList = contactHandler.getSpecialUsersList();
        this.allList = contactHandler.getAllList();
        this.recentList = contactHandler.handleRecents(api.chatSet);
        
        JsonArray array = api.batchGetContact(
                Arrays.asList(Const.API_SPECIAL_USER.toArray(new String[] {})));
        List<Contact> special = contactHandler.handle(array);
        System.out.println(special);
        
        log.info(Const.LOG_MSG_CONTACT_COUNT, memberCount, memberList.size());
        log.info(Const.LOG_MSG_OTHER_CONTACT_COUNT, groupList.size(),
                memberList.size(), specialUsersList.size(),
                publicUsersList.size());
    }
    
    @Override
    public void start() {
        if (receiveCallback != null && isLogin() && !pollStarted) {
            pollStarted = true;
            new Thread() {
                public void run() {
                    int logout = 0;
                    while (pollStarted) {
                        Utils.sleep(1000);
                        // retcode, selector
                        try {
                            int[] checkResponse = api.synccheck();
                            int retcode = checkResponse[0];
                            int selector = checkResponse[1];
                            log.debug("retcode: {}, selector: {}", retcode,
                                    selector);
                            switch (retcode) {
                                case 1100:
                                case 1101:
                                case 1102:
                                    logout++;
                                    if (logout > 8) {
                                        log.warn(Const.LOG_MSG_LOGOUT);
                                        close();
                                        if (receiveCallback != null) {
                                            receiveCallback.onReceiveError(
                                                    new LogicException(retcode,
                                                            Const.LOG_MSG_LOGOUT));
                                        }
                                    }
                                    break;
                                case 0:
                                    logout = 0;
                                    handle(selector);
                                    break;
                                default:
                                    logout = 0;
                                    log.debug("wxSync: {}\n",
                                            api.wxSync().toString());
                                    break;
                            }
                        } catch (TimeoutException e) {
                            // TODO
                        } catch (IOException e) {
                            if (e.getCause() instanceof TimeoutException) {
                                // Ignore
                            }
                        } catch (Exception e) {
                        
                        }
                    }
                };
            }.start();
        }
        
    }
    
    private void handle(int selector) throws Exception {
        switch (selector) {
            case 2:
                JsonObject dic = api.wxSync();
                if (null != dic) {
                    handle_msg(dic);
                }
                break;
            case 7:
                api.wxSync();
                break;
            case 0:
                Utils.sleep(1000);
                break;
            case 4:
                // 保存群聊到通讯录
                // 修改群名称
                // 新增或删除联系人
                // 群聊成员数目变化
                dic = api.wxSync();
                if (null != dic) {
                    handle_mod(dic);
                }
                break;
            default:
                break;
        }
    }
    
    private void handle_mod(JsonObject dic) {
        log.debug("handle modify");
        try {
            handle_contact(dic);
            handle_msg(dic);
        } catch (Throwable e) {
            if (receiveCallback != null) {
                receiveCallback.onReceiveError(e);
            }
        }
    }
    
    public Contact getGroupById(String gid) {
        Contact group = null;
        for (Contact g : groupList) {
            if (gid != null && gid.equals(g.UserName)) {
                group = g;
                break;
            }
        }
        if (group == null) { // 未保存的群聊
            JsonArray array = api.batchGetContact(Arrays.asList(gid));
            List<Contact> list = contactHandler.handle(array);
            if (!cn.ieclipse.smartim.Utils.isEmpty(list)) {
                group = list.get(0);
                this.groupList.add(group);
                if (modificationCallback != null) {
                    modificationCallback.onContactChanged(group);
                }
            }
        }
        return group;
    }
    
    private GroupFrom getGroupFrom(String gid, String uid) {
        GroupFrom from = new GroupFrom();
        
        Contact group = getGroupById(gid);
        if (group != null) {
            from.setGroup(group);
            from.setUser(group.getMember(uid));
            if (from.getUser() == null) {
                // 找不到群成员
                JsonArray array = api.batchGetContact(Arrays.asList(gid));
                List<Contact> list = contactHandler.handle(array);
                if (!cn.ieclipse.smartim.Utils.isEmpty(list)) {
                    int idx = groupList.indexOf(group);
                    group = list.get(0);
                    groupList.set(idx, group);
                    from.setUser(group.getMember(uid));
                    from.setNewbie(true);
                }
            }
            return from;
        }
        else {
            // 获取群失败, 有可能群被解散或网络访问失败
        }
        
        return from;
    }
    
    public UserFrom getUserFrom(String uid) {
        UserFrom from = new UserFrom();
        if (!cn.ieclipse.smartim.Utils.isEmpty(memberList)) {
            for (Contact t : memberList) {
                if (uid != null && uid.equals(t.UserName)) {
                    from.setUser(t);
                    break;
                }
            }
            if (from.getContact() == null) {
                JsonArray array = api.batchGetContact(Arrays.asList(uid));
                List<Contact> list = contactHandler.handle(array);
                if (!cn.ieclipse.smartim.Utils.isEmpty(list)) {
                    Contact c = list.get(0);
                    from.setUser(c);
                    memberList.add(c);
                    if (modificationCallback != null) {
                        modificationCallback.onContactChanged(c);
                    }
                }
            }
        }
        return from;
    }
    
    public AbstractFrom getFrom(WechatMessage msg) {
        try {
            if (msg.groupId != null) {
                return getGroupFrom(msg.groupId,
                        msg.src == null ? "" : msg.src);
            }
            else {
                UserFrom from = new UserFrom();
                if (!cn.ieclipse.smartim.Utils.isEmpty(memberList)) {
                    for (Contact t : memberList) {
                        if (msg.src != null && msg.src.equals(t.UserName)) {
                            from.setUser(t);
                            break;
                        }
                    }
                    
                }
                return from;
            }
        } catch (Exception e) {
            if (receiveCallback != null) {
                receiveCallback.onReceiveError(e);
            }
        }
        return null;
    }
    
    private void handle_contact(JsonObject dic) {
        JsonArray modContactList = dic.getAsJsonArray("ModContactList");
        for (JsonElement element : modContactList) {
            JsonObject m = element.getAsJsonObject();
            String username = m.get("UserName").getAsString();
            if (username.startsWith("@@")) { // group
                boolean in_list = false;
                
                Contact newg = contactHandler.handle(m);
                if (!cn.ieclipse.smartim.Utils.isEmpty(groupList)) {
                    for (int i = 0; i < groupList.size(); i++) {
                        Contact g = groupList.get(i);
                        if (username.equals(g.UserName)) {
                            in_list = true;
                            g = newg;
                            groupList.set(i, newg);
                        }
                    }
                }
                if (!in_list) {
                    groupList.add(newg);
                }
            }
            else if (username.startsWith("@")) {
                Contact c = contactHandler.handle(m);
                boolean in_list = false;
                for (int i = 0; i < memberList.size(); i++) {
                    Contact contact = memberList.get(i);
                    if (username.equals(contact.UserName)) {
                        in_list = true;
                        contact = c;
                        memberList.set(i, contact);
                        break;
                    }
                }
                if (!in_list) {
                    memberList.add(c);
                }
            }
        }
    }
    
    private void handle_msg(JsonObject json) {
        List<WechatMessage> msgs = msgHandler.handleAll(json);
        for (WechatMessage msg : msgs) {
            boolean handled = intercept(msg);
            AbstractFrom from = getFrom(msg);
            if (!handled) {
                if (receiveCallback != null) {
                    receiveCallback.onReceiveMessage(msg, from);
                }
            }
            else {
                System.out.println("intercept msg : " + msg);
            }
        }
    }
    
    private boolean intercept(IMessage msg) {
        boolean ret = false;
        for (MessageInterceptor interceptor : interceptors) {
            if (interceptor.handle(msg)) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    private WechatMessageHandler msgHandler = new WechatMessageHandler();
    
    @Override
    public int sendMessage(IMessage msg, IContact target) throws Exception {
        JsonObject ret = api.wxSendMessage(WechatMessage.MSGTYPE_TEXT,
                new Gson().toJson(msg));
        return 0;
    }
    
    public IMessage createMessage(int type, String msg, IContact target) {
        String clientMsgId = System.currentTimeMillis()
                + Utils.getRandomNumber(5);
        WechatMessage m = new WechatMessage();
        m.MsgType = type;
        m.Content = Utils.unicodeToUtf8(msg);
        m.FromUserName = getAccount().getUin();
        m.ToUserName = target.getUin();
        m.LocalID = clientMsgId;
        m.ClientMsgId = clientMsgId;
        return m;
    }
    
    public int sendMessage(String msg, String uin) {
        try {
            api.wxSendMessage(msg, uin);
            notifySend(0, uin, msg, null);
        } catch (Exception e) {
            notifySend(0, uin, msg, e);
        }
        return 0;
    }
    
    public int broadcast(String msg, Object... targets) {
        int ret = 0;
        if (targets != null) {
            for (Object target : targets) {
                if (target != null) {
                    try {
                        if (target instanceof IContact) {
                            api.wxSendMessage(msg,
                                    ((IContact) target).getUin());
                        }
                        ret++;
                    } catch (Exception e) {
                    
                    }
                }
            }
        }
        return ret;
    }
    
    @Override
    public IContact getAccount() {
        return accout;
    }
    
    public List<Contact> getGroupList() {
        return groupList;
    }
    
    public List<Contact> getMemberList() {
        return memberList;
    }
    
    public List<Contact> getPublicUsersList() {
        return publicUsersList;
    }
    
    public List<Contact> getSpecialUsersList() {
        return specialUsersList;
    }
    
    public List<Contact> getRecentList() {
        return recentList;
    }
    
    public Contact find(String uin, List<Contact> list) {
        return contactHandler.find(uin, list);
    }
    
    public static void main(String[] args) throws Exception {
        WechatClient client = new WechatClient();
        client.setWorkDir(new File("").getAbsoluteFile());
        client.setLoginCallback(new LoginCallback() {
            
            @Override
            public void onQrcode(String path) {
                System.out.println("qrcode:" + path);
            }
            
            @Override
            public void onLogin(boolean success, Exception e) {
                System.out.println("login:" + success + " exception:" + e);
            }
        });
        client.setReceiveCallback(new ReceiveCallback() {
            
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
        });
        System.out.println(System.currentTimeMillis());
        client.login();
        if (client.isLogin()) {
            client.init();
            
            client.start();
        }
    }
}
