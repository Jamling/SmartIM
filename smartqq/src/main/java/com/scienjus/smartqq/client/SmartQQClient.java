package com.scienjus.smartqq.client;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scienjus.smartqq.handler.DiscussHandler;
import com.scienjus.smartqq.handler.DiscussInfoHandler;
import com.scienjus.smartqq.handler.FriendHandler;
import com.scienjus.smartqq.handler.FriendStatusHandler;
import com.scienjus.smartqq.handler.GroupHandler;
import com.scienjus.smartqq.handler.GroupInfoHandler;
import com.scienjus.smartqq.handler.RecentHandler;
import com.scienjus.smartqq.handler.UserInfoHandler;
import com.scienjus.smartqq.handler.msg.DiscussMessageHandler;
import com.scienjus.smartqq.handler.msg.FriendMessageHandler;
import com.scienjus.smartqq.handler.msg.GroupMessageHandler;
import com.scienjus.smartqq.model.Category;
import com.scienjus.smartqq.model.Discuss;
import com.scienjus.smartqq.model.DiscussFrom;
import com.scienjus.smartqq.model.DiscussInfo;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.DiscussUser;
import com.scienjus.smartqq.model.Font;
import com.scienjus.smartqq.model.Friend;
import com.scienjus.smartqq.model.FriendFrom;
import com.scienjus.smartqq.model.FriendMessage;
import com.scienjus.smartqq.model.FriendStatus;
import com.scienjus.smartqq.model.Group;
import com.scienjus.smartqq.model.GroupFrom;
import com.scienjus.smartqq.model.GroupInfo;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.GroupUser;
import com.scienjus.smartqq.model.QQContact;
import com.scienjus.smartqq.model.QQMessage;
import com.scienjus.smartqq.model.Recent;
import com.scienjus.smartqq.model.UserInfo;

import cn.ieclipse.smartim.AbstractSmartClient;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.exception.LogicException;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.IMessage;
import cn.ieclipse.smartim.model.impl.AbstractFrom;
import cn.ieclipse.smartim.model.impl.AbstractMessage;

public class SmartQQClient extends AbstractSmartClient {
    public static boolean DEBUG = true;
    // 日志
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SmartQQClient.class);
    private SmartQQApi api;
    
    // 消息处理器
    private FriendMessageHandler friendMessageHandler = new FriendMessageHandler();
    private GroupMessageHandler groupMessageHandler = new GroupMessageHandler();
    private DiscussMessageHandler discussMessageHandler = new DiscussMessageHandler();
    
    // 联系人处理器
    private UserInfoHandler userInfoHandler = new UserInfoHandler();
    private GroupInfoHandler groupInfoHandler = new GroupInfoHandler();
    private DiscussInfoHandler discussInfoHandler = new DiscussInfoHandler();
    private FriendHandler friendHandler = new FriendHandler();
    private GroupHandler groupHandler = new GroupHandler();
    private DiscussHandler discussHandler = new DiscussHandler();
    
    // 保存的数据
    public List<Recent> recents;
    public List<QQContact> recents2;
    public List<Group> groups;
    public List<Friend> temps;
    public List<Discuss> discusses;
    public List<Category> categories;
    public Map<Long, GroupInfo> ginfos;
    public Map<Long, DiscussInfo> dinfos;
    public UserInfo account;
    
    public SmartQQClient() {
        name = "SmartQQ";
        api = new SmartQQApi();
    }
    
    public void start() {
        if (receiveCallback != null && isLogin() && !pollStarted) {
            this.pollStarted = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!pollStarted) {
                            return;
                        }
                        try {
                            JsonArray array = api.pollMessage();
                            handleMessage(array);
                        } catch (IOException e) {
                            // 忽略SocketTimeoutException
                            if (!(e.getCause() instanceof SocketTimeoutException)) {
                                LOGGER.debug(e.getMessage());
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            if (e instanceof LogicException) {
                                int code = ((LogicException) e).getCode();
                                if (code == 103 || code == 100100
                                        || code == 100001) {
                                    close();
                                    if (receiveCallback != null) {
                                        receiveCallback.onReceiveError(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }).start();
        }
    }
    
    public QQMessage handleMessage(JsonObject message) {
        String type = message.get("poll_type").getAsString();
        if ("message".equals(type)) {
            FriendMessage m = (FriendMessage) friendMessageHandler
                    .handle(message.getAsJsonObject("value"));
            return m;
        }
        else if ("group_message".equals(type)) {
            GroupMessage m = (GroupMessage) groupMessageHandler
                    .handle(message.getAsJsonObject("value"));
            return m;
        }
        else if ("discu_message".equals(type)) {
            DiscussMessage m = (DiscussMessage) discussMessageHandler
                    .handle(message.getAsJsonObject("value"));
            return m;
        }
        return null;
    }
    
    private void handleMessage(JsonArray array) {
        for (int i = 0; array != null && i < array.size(); i++) {
            JsonObject message = (JsonObject) array.get(i);
            QQMessage m = handleMessage(message);
            if (m != null) {
                AbstractFrom from = parseFrom(m);
                if (from != null && from.getContact() != null) {
                    Recent r = new Recent();
                    long uin = Long.parseLong(from.getContact().getUin());
                    r.setUin(uin);
                    if (from instanceof FriendFrom) {
                        r.setType(0);
                    }
                    else if (from instanceof GroupFrom) {
                        r.setType(1);
                    }
                    else if (from instanceof DiscussFrom) {
                        r.setType(2);
                    }
                    if (!getRecentList().contains(r)) {
                        getRecentList().add(0, r);
                    }
                }
                notifyReceive(m, from);
            }
        }
    }
    
    public void login() {
        try {
            String path = api.getQRCode();
            if (loginCallback != null) {
                loginCallback.onQrcode(path);
            }
            api.login();
            isLogin = true;
            if (loginCallback != null) {
                try {
                    loginCallback.onLogin(isLogin, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("登录失败", e);
            if (loginCallback != null) {
                loginCallback.onLogin(false, e);
            }
        }
    }
    
    @Override
    public void setWorkDir(File path) {
        super.setWorkDir(path);
        api.setWorkDir(path);
    }
    
    @Override
    public void init() throws Exception {
        UserInfo t = userInfoHandler.handle(api.getAccountInfo());
        if (t != null) {
            this.account = t;
        }
        List<Category> tc = friendHandler
                .handleCategoryList(api.getFriendListWithCategory());
        if (!isEmpty(tc)) {
            this.categories = tc;
        }
        List<Group> tg = groupHandler.handle(api.getGroupList());
        if (!isEmpty(tg)) {
            this.groups = tg;
        }
        
        List<Discuss> td = discussHandler.handle(api.getDiscussList());
        if (!isEmpty(td)) {
            this.discusses = td;
        }
        
        List<Recent> tr = new RecentHandler().handle(api.getRecentList());
        if (!isEmpty(tr)) {
            this.recents = tr;
        }
        
        this.recents2 = parseRecents(this.recents);
    }
    
    public List<Category> getFriendListWithCategory() {
        // if (isEmpty(categories)) {
        // try {
        // categories = friendHandler
        // .handleCategoryList(api.getFriendListWithCategory());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        return categories;
    }
    
    public List<Group> getGroupList() {
        // if (isEmpty(groups)) {
        // try {
        // groups = groupHandler.handle(api.getGroupList());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        return groups;
    }
    
    public List<Discuss> getDiscussList() {
        // if (isEmpty(discusses)) {
        // try {
        // discusses = discussHandler.handle(api.getDiscussList());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        return discusses;
    }
    
    // public List<Friend> getFriendList() {
    // return api.getFriendList();
    // }
    
    public UserInfo getFriendInfo(long friendId) {
        try {
            return userInfoHandler.handle(api.getFriendInfo(friendId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Recent> getRecentList() {
        // if (isEmpty(recents)) {
        // try {
        // recents = new RecentHandler().handle(api.getRecentList());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        return recents;
    }
    
    public List<QQContact> getRecents2() {
        return recents2;
    }
    
    public long getQQById(long friendId) {
        try {
            return api.getQQById(friendId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<FriendStatus> getFriendStatus() {
        try {
            return new FriendStatusHandler().handle(api.getFriendStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public GroupInfo getGroupInfo(long groupCode) {
        try {
            return groupInfoHandler.handle(api.getGroupInfo(groupCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public DiscussInfo getDiscussInfo(long discussId) {
        try {
            return discussInfoHandler.handle(api.getDiscussInfo(discussId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void saveAuthInfo() {
        api.saveAuthInfo();
    }
    
    public void readAuthInfo(String file) {
        api.readAuthInfo();
    }
    
    public void close() {
        try {
            this.isLogin = false;
            this.pollStarted = false;
            this.isClose = true;
            api.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        clear(categories);
        clear(groups);
        clear(discusses);
        clear(ginfos);
        clear(dinfos);
    }
    
    // ----->
    public boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
    
    public boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
    
    public void clear(Collection<?> c) {
        if (!isEmpty(c)) {
            c.clear();
        }
    }
    
    public boolean isEmpty(Map<?, ?> c) {
        return c == null || c.isEmpty();
    }
    
    public void clear(Map<?, ?> c) {
        if (!isEmpty(c)) {
            c.clear();
        }
    }
    
    public UserInfo getAccount() {
        return account;
    }
    
    public Recent getRecent(int type, long uin) {
        List<Recent> list = getRecentList();
        int idx = -1;
        for (Recent r : list) {
            idx++;
            if (r.getType() == type && r.getUin() == uin) {
                return r;
            }
        }
        Recent r = new Recent();
        r.setType(type);
        r.setUin(uin);
        list.add(0, r);
        return r;
    }
    
    public Friend getFriend(long uin) {
        if (categories != null) {
            for (Category c : categories) {
                List<Friend> list = c.getFriends();
                for (Friend f : list) {
                    if (f.getUserId() == uin) {
                        return f;
                    }
                }
            }
        }
        if (temps != null) {
            for (Friend f : temps) {
                if (f.getUserId() == uin) {
                    return f;
                }
            }
        }
        return null;
    }
    
    public Group getGroup(long uin) {
        if (isEmpty(groups)) {
            try {
                groups = groupHandler.handle(api.getGroupList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (groups != null) {
            for (Group g : groups) {
                if (g.id == uin) {
                    return g;
                }
            }
        }
        // groups out of date
        // try {
        // groups = groupHandler.handle(api.getGroupList());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // if (groups != null) {
        // for (Group g : groups) {
        // if (g.id == uin) {
        // if (modificationCallback != null) {
        // modificationCallback.onContactChanged(g);
        // }
        // return g;
        // }
        // }
        // }
        
        return null;
    }
    
    public Discuss getDiscuss(long uin) {
        if (isEmpty(discusses)) {
            try {
                discusses = discussHandler.handle(api.getDiscussList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (discusses != null) {
            for (Discuss g : discusses) {
                if (g.id == uin) {
                    return g;
                }
            }
        }
        // out of date
        // try {
        // discusses = discussHandler.handle(api.getDiscussList());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // if (discusses != null) {
        // for (Discuss g : discusses) {
        // if (g.id == uin) {
        // if (modificationCallback != null) {
        // modificationCallback.onContactChanged(g);
        // }
        // return g;
        // }
        // }
        // }
        
        return null;
    }
    
    public GroupInfo getGroupInfo(Group group) {
        GroupInfo info = null;
        if (ginfos != null) {
            info = ginfos.get(group.id);
        }
        // out of date
        if (info == null) {
            try {
                info = getGroupInfo(group.code);
                if (modificationCallback != null) {
                    modificationCallback.onContactChanged(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ginfos == null) {
                ginfos = new HashMap<>();
            }
            ginfos.put(group.id, info);
        }
        return info;
    }
    
    public GroupUser getGroupUser(GroupMessage m) {
        Group g = getGroup(m.getGroupId());
        GroupInfo info = getGroupInfo(g);
        if (info != null && info.getUsers() != null) {
            for (GroupUser u : info.getUsers()) {
                if (u.getUin().equals(String.valueOf(m.getUserId()))) {
                    return u;
                }
            }
        }
        return null;
    }
    
    public DiscussInfo getDiscussInfo(Discuss discuss) {
        DiscussInfo info = null;
        if (dinfos != null) {
            info = dinfos.get(discuss.id);
        }
        // out of date
        if (info == null) {
            try {
                info = getDiscussInfo(discuss.id);
                if (modificationCallback != null) {
                    modificationCallback.onContactChanged(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dinfos == null) {
                dinfos = new HashMap<>();
            }
            dinfos.put(discuss.id, info);
        }
        return info;
    }
    
    public DiscussUser getDiscussUser(DiscussMessage m) {
        Discuss g = getDiscuss(m.getDiscussId());
        DiscussInfo info = getDiscussInfo(g);
        if (info != null && info.getUsers() != null) {
            for (DiscussUser u : info.getUsers()) {
                if (u.getUin().equals(String.valueOf(m.getUserId()))) {
                    return u;
                }
            }
        }
        return null;
    }
    
    public AbstractFrom parseFrom(QQMessage m) {
        if (m != null) {
            if (m instanceof FriendMessage) {
                return parseFrom((FriendMessage) m);
            }
            else if (m instanceof GroupMessage) {
                return parseFrom((GroupMessage) m);
            }
            else if (m instanceof DiscussMessage) {
                return parseFrom((DiscussMessage) m);
            }
        }
        return null;
    }
    
    public FriendFrom parseFrom(FriendMessage friendMessage) {
        FriendFrom from = new FriendFrom();
        Friend f = getFriend(friendMessage.getUserId());
        if (f == null) {
            // long qq = getQQById(message.getUserId());
            f = new Friend();
            f.setUserId(friendMessage.getUserId());
            f.setMarkname("未知用户" + f.getUserId());
            if (temps == null) {
                temps = new ArrayList<>();
            }
            temps.add(f);
            from.setNewbie(true);
        }
        from.setFriend(f);
        return from;
    }
    
    public GroupFrom parseFrom(final GroupMessage message) {
        GroupFrom from = new GroupFrom();
        Group g = getGroup(message.getGroupId());
        boolean newGroup = false;
        if (g == null) {
            g = new Group();
            g.code = message.getGroupId();
            g.id = g.code;
            newGroup = true;
        }
        
        GroupInfo info = null;
        if (ginfos == null) {
            try {
                info = getGroupInfo(g.code);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ginfos = new HashMap<>();
            ginfos.put(g.id, info);
        }
        else {
            info = getGroupInfo(g);
        }
        if (newGroup) {
            g.name = info.getName();
        }
        GroupUser gu = info.getGroupUser(message.getUserId());
        if (gu == null) {
            try {
                info = getGroupInfo(g.code);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (info != null) {
                ginfos.put(g.id, info);
                gu = info.getGroupUser(message.getUserId());
                if (gu == null) {
                    gu = new GroupUser();
                    gu.setUin(message.getUserId());
                    gu.setCard(String.valueOf(message.getUserId()));
                    info.getUsers().add(gu);
                    gu.setUnknown(true);
                }
                else {
                    from.setNewbie(true);
                }
            }
        }
        from.setGroupUser(gu);
        from.setGroup(info);
        if (newGroup) {
            if (modificationCallback != null) {
                modificationCallback.onContactChanged(g);
            }
        }
        return from;
    }
    
    public DiscussFrom parseFrom(final DiscussMessage message) {
        DiscussFrom from = new DiscussFrom();
        Discuss g = getDiscuss(message.getDiscussId());
        boolean newGroup = false;
        if (g == null) {
            g = new Discuss();
            g.id = message.getDiscussId();
            newGroup = true;
        }
        DiscussInfo info = null;
        if (dinfos == null) {
            try {
                info = getDiscussInfo(g.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dinfos = new HashMap<>();
            dinfos.put(g.id, info);
        }
        else {
            info = getDiscussInfo(g);
        }
        if (newGroup) {
            g.name = info.getName();
        }
        DiscussUser gu = info.getDiscussUser(message.getUserId());
        if (gu == null) {
            try {
                info = getDiscussInfo(g.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (info != null) {
                dinfos.put(g.id, info);
                gu = info.getDiscussUser(message.getUserId());
                if (gu == null) {
                    gu = new DiscussUser();
                    gu.uin = (message.getUserId());
                    gu.nick = (String.valueOf(message.getUserId()));
                    info.getUsers().add(gu);
                    gu.setUnknown(true);
                }
                else {
                    from.setNewbie(true);
                }
            }
        }
        from.setDiscussUser(gu);
        from.setDiscuss(info);
        if (newGroup) {
            if (modificationCallback != null) {
                modificationCallback.onContactChanged(g);
            }
        }
        return from;
    }
    
    public List<QQContact> parseRecents(List<Recent> list) {
        List<QQContact> ret = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                IContact c = getRecentTarget(list.get(i));
                if (c != null && c instanceof QQContact) {
                    ret.add((QQContact) c);
                }
            }
        }
        return ret;
    }
    
    public IContact getRecentTarget(Recent r) {
        if (r.getType() == 0) {
            Friend f = getFriend(r.getUin());
            if (f == null) {
                f = new Friend();
                f.setUserId(r.getUin());
                f.setMarkname("unknown " + r.getUin());
                f.setUnknown(true);
            }
            return f;
        }
        else if (r.getType() == 1) {
            Group g = getGroup(r.getUin());
            return g;
        }
        else if (r.getType() == 2) {
            Discuss d = getDiscuss(r.getUin());
            return d;
        }
        return null;
    }
    
    public static void setDebug(boolean debug) {
        DEBUG = debug;
        SmartQQApi.DEBUG = debug;
    }
    
    public static String getName(Object obj) {
        String name = null;
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Friend) {
            Friend f = (Friend) obj;
            name = f.getMarkname();
            if (name == null || name.isEmpty()) {
                name = f.getNickname();
            }
            if (name == null || name.isEmpty()) {
                name = String.valueOf(f.getUserId());
            }
        }
        else if (obj instanceof Group) {
            Group g = ((Group) obj);
            name = g.getName();
        }
        else if (obj instanceof Discuss) {
            Discuss d = (Discuss) obj;
            name = d.getName();
        }
        else if (obj instanceof GroupUser) {
            GroupUser gu = (GroupUser) obj;
            name = gu.getCard();
            if (name == null || name.isEmpty()) {
                name = gu.getNick();
            }
            if (name == null || name.isEmpty()) {
                name = String.valueOf(gu.getUin());
            }
        }
        else if (obj instanceof DiscussUser) {
            DiscussUser gu = (DiscussUser) obj;
            name = gu.getName();
            if (name == null || name.isEmpty()) {
                name = String.valueOf(gu.getUin());
            }
        }
        else if (obj instanceof Category) {
            name = ((Category) obj).getName();
        }
        return name;
    }
    
    @Override
    public int sendMessage(IMessage msg, IContact target) {
        Long uin = Long.parseLong(target.getUin());
        String content = ((QQMessage) msg).getContent();
        if (target instanceof Friend || target instanceof UserInfo) {
            sendMessageToFriend(uin, content);
        }
        else if (target instanceof Group || target instanceof GroupInfo) {
            sendMessageToGroup(uin, content);
        }
        else if (target instanceof Discuss || target instanceof DiscussInfo) {
            sendMessageToDiscuss(uin, content);
        }
        return 0;
    }
    
    public void sendMessageToGroup(long groupId, String msg) {
        try {
            api.sendMessageToGroup(groupId, msg);
            notifySend(1, String.valueOf(groupId), msg, null);
        } catch (Exception e) {
            notifySend(1, String.valueOf(groupId), msg, e);
        }
    }
    
    public void sendMessageToDiscuss(long discussId, String msg) {
        try {
            api.sendMessageToDiscuss(discussId, msg);
            notifySend(2, String.valueOf(discussId), msg, null);
        } catch (Exception e) {
            notifySend(2, String.valueOf(discussId), msg, e);
        }
    }
    
    public void sendMessageToFriend(long friendId, String msg) {
        try {
            api.sendMessageToFriend(friendId, msg);
            notifySend(0, String.valueOf(friendId), msg, null);
        } catch (Exception e) {
            notifySend(0, String.valueOf(friendId), msg, e);
        }
    }
    
    public QQMessage createMessage(String msg, IContact target) {
        long uin = Long.parseLong(target.getUin());
        if (target instanceof Friend || target instanceof UserInfo) {
            QQMessage ret = new FriendMessage();
            ret.setContent(msg);
            ret.setTime(System.currentTimeMillis());
            ret.setFont(Font.DEFAULT_FONT);
            ret.setUserId(getAccount().getId());
            return ret;
        }
        else if (target instanceof Group || target instanceof GroupInfo) {
            GroupMessage ret = new GroupMessage();
            ret.setContent(msg);
            ret.setTime(System.currentTimeMillis());
            ret.setFont(Font.DEFAULT_FONT);
            ret.setUserId(getAccount().getId());
            ret.setGroupId(uin);
            return ret;
        }
        else if (target instanceof Discuss || target instanceof DiscussInfo) {
            DiscussMessage ret = new DiscussMessage();
            ret.setContent(msg);
            ret.setTime(System.currentTimeMillis());
            ret.setFont(Font.DEFAULT_FONT);
            ret.setUserId(getAccount().getId());
            ret.setDiscussId(uin);
            return ret;
        }
        return null;
    }
    
    public int broadcast(String msg, Object... targets) {
        int ret = 0;
        if (targets != null) {
            for (Object target : targets) {
                if (target != null) {
                    try {
                        if (target instanceof Friend) {
                            api.sendMessageToFriend(
                                    ((Friend) target).getUserId(), msg);
                        }
                        else if (target instanceof Group) {
                            api.sendMessageToGroup(((Group) target).id, msg);
                        }
                        else if (target instanceof Discuss) {
                            api.sendMessageToDiscuss(((Discuss) target).id,
                                    msg);
                        }
                        ret++;
                    } catch (Exception e) {
                    
                    }
                }
            }
        }
        return ret;
    }
    
    public static void main(String[] args) throws Exception {
        SmartQQClient client = new SmartQQClient();
        client.setReceiveCallback(new ReceiveCallback() {
            @Override
            public void onReceiveMessage(AbstractMessage message,
                    AbstractFrom from) {
                QQMessage m = (QQMessage) message;
                String date = new SimpleDateFormat("HH:mm:ss")
                        .format(m.getTime());
                String content = String.format("%s %s %s", date, from.getName(),
                        m.getContent());
                System.out.println(content);
                if (from.isNewbie()) {
                    System.out.println("new friend");
                }
            }
            
            @Override
            public void onReceiveError(Throwable e) {
                e.printStackTrace();
            }
        });
        
        client.login();
        while (!client.isLogin()) {
            Thread.sleep(1000);
        }
        client.init();
        client.start();
    }
}
