package io.github.biezhi.wechat.api;

import cn.ieclipse.smartim.AbstractSmartClient;
import cn.ieclipse.smartim.callback.LoginCallback;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.exception.LogicException;
import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.IMessage;
import cn.ieclipse.smartim.model.impl.AbstractContact;
import cn.ieclipse.smartim.model.impl.AbstractFrom;
import cn.ieclipse.smartim.model.impl.AbstractMessage;
import cn.ieclipse.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.biezhi.wechat.Utils;
import io.github.biezhi.wechat.handler.*;
import io.github.biezhi.wechat.handler.msg.AppMsgXmlHandler;
import io.github.biezhi.wechat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Created by Jamling on 2017/7/17.
 */
public class WechatClient extends AbstractSmartClient {
    private static final Logger log = LoggerFactory
            .getLogger(WechatClient.class);
            
    private static WechatClient instance;
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
    
    public static WechatClient getInstance() {
        return instance;
    }
    
    public WechatClient() {
        this(null);
    }
    
    public WechatClient(Proxy proxy) {
        Environment environment = Environment.of("classpath:config.properties");
        api = new WechatApi(environment, proxy);
        addMessageInterceptor(new TypeMessageInterceptor());
        addMessageInterceptor(new GroupMessageInterceptor());
        addMessageInterceptor(new DecodeMessageInterceptor());
        instance = this;
    }
    
    private boolean waitForLogin() {
        int maxCount = 30;
        while (maxCount > 0) {
            Utils.sleep(maxCount < 5 ? 500 : 1000);
            log.info(Const.LOG_MSG_SCAN_QRCODE);
            // 这是一个阻塞方法
            if (!api.waitforlogin(1)) {
                maxCount--;
                continue;
            }
            File avatar = new File(workDir, "avatar.jpg");
            if (loginCallback != null && avatar.exists()) {
                loginCallback.onAvatar(avatar.getAbsolutePath());
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
        api.close();
    }
    
    @Override
    public void setWorkDir(File path) {
        super.setWorkDir(path);
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
        JsonArray array = api.batchGetContact(
                Arrays.asList(Const.API_SPECIAL_USER.toArray(new String[] {})));
        List<Contact> special = contactHandler.handle(array);
        this.specialUsersList = special;
        
        this.groupList = contactHandler.getGroupList();
        // fetch group member
        if (this.groupList != null && !this.groupList.isEmpty()) {
            List<String> gids = new ArrayList<>(this.groupList.size());
            for(Contact c : this.groupList) {
                gids.add(c.getUin());
            }
            JsonArray garray = api.batchGetContact(gids);
            this.groupList = contactHandler.handle(garray);
        }
        this.memberList = contactHandler.getMemberList();
        this.publicUsersList = contactHandler.getPublicUsersList();
        List<Contact> special2 = contactHandler.getSpecialUsersList();
        this.specialUsersList.addAll(special2);
        this.allList = contactHandler.getAllList();
        this.recentList = contactHandler.handleRecents(api.chatSet,
                this.specialUsersList);
                
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
                            // log.debug("retcode: {}, selector: {}", retcode,
                            // selector);
                            switch (retcode) {
                                case 1100:
                                case 1101:
                                case 1102:
                                    logout++;
                                    if (logout > 3) {
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
            if (!StringUtils.isEmpty(list)) {
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
                if (!StringUtils.isEmpty(list)) {
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
    
    public Contact getContact(String uid) {
        Contact contact = null;
        if (!uid.equals(getAccount().getUin())) {
            for (Contact t : memberList) {
                if (uid != null && uid.equals(t.UserName)) {
                    contact = t;
                    break;
                }
            }
            if (contact == null) {
                for (Contact t : publicUsersList) {
                    if (uid != null && uid.equals(t.UserName)) {
                        contact = t;
                        break;
                    }
                }
            }
            if (contact == null) {
                JsonArray array = api.batchGetContact(Arrays.asList(uid));
                List<Contact> list = contactHandler.handle(array);
                if (!StringUtils.isEmpty(list)) {
                    Contact c = list.get(0);
                    contact = c;
                    if (c.isPublic()) {
                        publicUsersList.add(c);
                    }
                    else {
                        memberList.add(c);
                    }
                    if (modificationCallback != null) {
                        modificationCallback.onContactChanged(c);
                    }
                }
            }
        }
        return contact;
    }
    
    public AbstractFrom getFrom(WechatMessage msg) {
        try {
            if (msg.groupId != null) {
                return getGroupFrom(msg.groupId,
                        msg.src == null ? "" : msg.src);
            }
            else {
                UserFrom from = new UserFrom();
                if (msg.src.equals(accout.UserName)) {
                    from.setUser(getContact(msg.ToUserName));
                    from.setTarget(accout);
                    from.setOut();
                }
                else {
                    from.setUser(getContact(msg.src));
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
                if (!StringUtils.isEmpty(groupList)) {
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
                    if (modificationCallback != null) {
                        modificationCallback.onContactChanged(newg);
                    }
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
                    if (modificationCallback != null) {
                        modificationCallback.onContactChanged(c);
                    }
                }
            }
        }
    }
    
    public IMessage handleMessage(String raw) {
        WechatMessage msg = msgHandler.handle(raw);
        if (!intercept(msg)) {
            return msg;
        }
        return null;
    }
    
    public void handle_msg(JsonObject json) {
        List<WechatMessage> msgs = msgHandler.handleAll(json);
        for (WechatMessage msg : msgs) {
            if (msg.MsgType == WechatMessage.MSGTYPE_STATUSNOTIFY
                    && msg.StatusNotifyCode == 4
                    && !StringUtils.isEmpty(msg.StatusNotifyUserName)) {
                // String r = new InitMsgXmlHandler(msg.Content).getRecents();
                this.recentList = contactHandler.handleRecents(
                        msg.StatusNotifyUserName, this.specialUsersList);
                if (modificationCallback != null) {
                    modificationCallback.onContactChanged((IContact) null);
                }
            }
            boolean handled = intercept(msg);
            if (!handled) {
                AbstractFrom from = getFrom(msg);
                if (from != null && from.getContact() != null) {
                    if (!getRecentList().contains(from.getContact())) {
                        getRecentList().add(0, (Contact) from.getContact());
                    }
                }
                notifyReceive(msg, from);
            }
            else {
                log.info("intercept msg : " + msg);
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
    public int sendMessage(IMessage msg, IContact target) {
        if (target instanceof AbstractContact) {
            ((AbstractContact) target).setLastMessage(msg);
        }
        String uin = target.getUin();
        WechatMessage m = (WechatMessage) msg;
        String text = StringUtils.isEmpty(m.text) ? m.Content : m.text;
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("Type", m.MsgType);
            body.put("Content", m.Content);
            body.put("FromUserName", m.FromUserName);
            body.put("ToUserName", m.ToUserName);
            body.put("MediaId", m.MediaId);
            JsonObject ret = api.wxSendMessage(body);
            notifySend(0, uin, text, null);
        } catch (Exception e) {
            notifySend(0, uin, text, e);
        }
        return 0;
    }
    
    public WechatMessage createMessage(int type, String msg, IContact target) {
        WechatMessage m = new WechatMessage();
        m.MsgType = type <= 0 ? WechatMessage.MSGTYPE_TEXT : type;
        m.Content = Utils.unicodeToUtf8(msg);
        m.FromUserName = getAccount().getUin();
        m.ToUserName = target.getUin();
        m.CreateTime = System.currentTimeMillis() / 1000;
        return m;
    }
    
    /**
     * 上传文件
     * 
     * @param file
     *            文件
     * @param mime
     *            mime-type
     * @param media
     *            media type, 图片为pic，文档为doc，如果null，则根据文件后缀自动计算
     * @return {@link UploadInfo}
     */
    public UploadInfo uploadMedia(File file, String mime, String media) {
        try {
            return api.wxUploadMedia(false, file, mime, media);
        } catch (Exception e) {
            return null;
        }
    }
    
    public String getMediaLink(WechatMessage m, File file) {
        try {
            int msgType = m.MsgType;
            String msgId = m.MsgId;
            if (WechatMessage.MSGTYPE_IMAGE == msgType
                    || WechatMessage.MSGTYPE_EMOTICON == msgType) {
                return api.wxGetMsgImg(msgId, file);
            }
            else if (WechatMessage.MSGTYPE_APP == msgType) {
                return api.wxGetMsgMedia(m, file);
            }
            else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    public String createFileMsgContent(File file, String mediaId) {
        return new AppMsgXmlHandler().encode(file, mediaId);
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
    
    public String getQueryString() {
        if (api != null && api.session != null) {
            return String.format("sid=%s&skey=%s", api.session.getSid(),
                    api.session.getSkey());
        }
        return null;
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
