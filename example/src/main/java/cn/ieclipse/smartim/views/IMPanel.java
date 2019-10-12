package cn.ieclipse.smartim.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import cn.ieclipse.smartim.IMHistoryManager;
import cn.ieclipse.smartim.SmartClient;
import cn.ieclipse.smartim.actions.DisconnectAction;
import cn.ieclipse.smartim.actions.LoginAction;
import cn.ieclipse.smartim.actions.MockConsoleAction;
import cn.ieclipse.smartim.actions.SettingsAction;
import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.smartim.common.RestUtils;
import cn.ieclipse.smartim.console.ClosableTabHost;
import cn.ieclipse.smartim.console.IMChatConsole;
import cn.ieclipse.smartim.console.MockChatConsole;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.util.StringUtils;

/**
 * Created by Jamling on 2017/7/11.
 */
public abstract class IMPanel extends JSplitPane implements ClosableTabHost.Callback {

    protected JTabbedPane tabbedChat;
    protected IMContactView left;
    protected String welcome;

    public IMPanel() {
        initUI();
    }

    private void initUI() {

        JPanel pLeft = new JPanel();
        pLeft.setLayout(new BorderLayout(0, 0));
        JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        toolBar1.setOrientation(SwingConstants.VERTICAL);
        initToolBar1(toolBar1);
        pLeft.add(toolBar1, BorderLayout.WEST);

        left = createContactsUI();
        left.onLoadContacts(false);
        pLeft.add(left, BorderLayout.CENTER);
        setLeftComponent(pLeft);

        JPanel pRight = new JPanel();
        pRight.setLayout(new BorderLayout(0, 0));

        tabbedChat = new ClosableTabHost(this);
        pRight.add(tabbedChat, BorderLayout.CENTER);

        setRightComponent(pRight);

        setResizeWeight(0.3);
        setDividerLocation(250);
    }

    protected void initToolBar1(JToolBar toolBar) {
        toolBar.add(new LoginAction(this));
        toolBar.add(new DisconnectAction(this));
        toolBar.add(new SettingsAction(this));
        MockConsoleAction test = new MockConsoleAction(this);
        // toolBar.add(test);
    }

    public boolean isLeftHidden() {
        boolean v = left.getPanel().isVisible();
        return !v;
    }

    public void setLeftHide(boolean select) {
        left.getPanel().setVisible(!select);
    }

    public abstract SmartClient getClient();

    public abstract IMContactView createContactsUI();

    public abstract IMChatConsole createConsoleUI(IContact contact);

    protected Map<String, IMChatConsole> consoles = new HashMap<>();

    public IMChatConsole findConsole(IContact contact, boolean add) {
        return consoles.get(contact.getUin());
    }

    public void randBling() {
        int size = tabbedChat.getComponentCount();
        int i = new Random().nextInt(size);
        String name = "Random";
        if (i >= 0 && tabbedChat instanceof ClosableTabHost) {
            ((ClosableTabHost)tabbedChat).bling(i, name);
        }
    }

    public void highlight(IMChatConsole console) {
        int i = tabbedChat.indexOfComponent(console);
        if (i >= 0 && tabbedChat instanceof ClosableTabHost) {
            ((ClosableTabHost)tabbedChat).bling(i, console.getName());
        }
    }

    public boolean isCurrent(final IMChatConsole console) {
        if (console != null) {
            if (console == tabbedChat.getSelectedComponent()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrent(final IContact contact) {
        if (contact != null) {
            Object o = tabbedChat.getSelectedComponent();
            IMChatConsole console = (IMChatConsole)o;
            if (o != null && o instanceof IMChatConsole) {
                return contact.getUin().equals(console.getUin());
            }
        }
        return false;
    }

    public IMChatConsole findConsoleById(String id, boolean show) {
        int count = tabbedChat.getTabCount();
        for (int i = 0; i < count; i++) {
            if (tabbedChat.getComponentAt(i) instanceof IMChatConsole) {
                IMChatConsole t = (IMChatConsole)tabbedChat.getComponentAt(i);
                if (id.equals(t.getUin())) {
                    if (show) {
                        tabbedChat.setSelectedIndex(i);
                    }
                    return t;
                }
            }
        }
        return null;
    }

    public void onDoubleClick(Object obj) {
        SmartClient client = getClient();
        if (client.isClose() || !client.isLogin()) {
            LOG.sendNotification("错误", "连接已断开，请重新登录");
            return;
        }
        if (obj instanceof IContact) {
            openConsole((IContact)obj);
        }
    }

    public void openConsole(IContact contact) {
        IMChatConsole console = findConsoleById(contact.getUin(), true);
        if (console == null) {
            console = createConsoleUI(contact);
            if (StringUtils.isEmpty(console.getName())) {
                // TODO set name in console
                console.setName(contact.getName());
            }
            tabbedChat.addTab(console.getName(), console);
            consoles.put(console.getUin(), console);
            tabbedChat.setSelectedComponent(console);
        }
        console.clearUnread();
    }

    public void addConsole(IMChatConsole console) {
        tabbedChat.addTab(console.getName(), console);
        consoles.put(console.getUin(), console);
        tabbedChat.setSelectedComponent(console);
    }

    public void close() {
        getClient().close();
        closeAllChat();
        left.notifyLoadContacts(false);
    }

    public void closeAllChat() {
        int count = tabbedChat.getTabCount();
        for (int i = 0; i < count; i++) {
            tabbedChat.remove(0);
        }
        consoles.clear();
        IMHistoryManager.getInstance().flush();
    }

    @Override
    public void removeTabAt(int index) {
        IMChatConsole console = null;
        Component comp = tabbedChat.getComponentAt(index);
        if (comp instanceof IMChatConsole) {
            console = (IMChatConsole)comp;
            consoles.remove(console.getUin());
        }
        tabbedChat.removeTabAt(index);
    }

    public java.util.List<IMChatConsole> getConsoleList() {
        java.util.List<IMChatConsole> list = new ArrayList<>();
        if (tabbedChat != null) {
            int count = tabbedChat.getTabCount();
            for (int i = 0; i < count; i++) {
                if (tabbedChat.getComponentAt(i) instanceof IMChatConsole) {
                    IMChatConsole t = (IMChatConsole)tabbedChat.getComponentAt(i);
                    list.add(t);
                }
            }
        }
        return list;
    }

    public void initContacts() {
        left.initContacts();
    }

    public void notifyUpdateContacts(int index, boolean force) {
        left.notifyUpdateContacts(index, force);
    }

    public void loadWelcome(final String im) {
        new Thread() {
            @Override
            public void run() {
                welcome = RestUtils.getWelcome(im);
            }
        }.start();
    }

    public String getWelcome() {
        if (welcome == null) {
            return "";
        }
        return welcome;
    }
}
