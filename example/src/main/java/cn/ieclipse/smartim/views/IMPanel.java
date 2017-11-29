package cn.ieclipse.smartim.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import cn.ieclipse.smartim.SmartClient;
import cn.ieclipse.smartim.callback.impl.DefaultLoginCallback;
import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.smartim.console.ClosableTabHost;
import cn.ieclipse.smartim.console.IMChatConsole;
import cn.ieclipse.smartim.model.IContact;
import icons.SmartIcons;

import javax.swing.ImageIcon;

/**
 * Created by Jamling on 2017/7/11.
 */
public abstract class IMPanel extends JSplitPane {
    
    protected JTabbedPane tabbedChat;
    protected IMContactView left;
    
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
        
        tabbedChat = new ClosableTabHost();
        pRight.add(tabbedChat, BorderLayout.CENTER);
        
        setRightComponent(pRight);
        
        setResizeWeight(0.3);
        setDividerLocation(250);
    }
    
    protected void initToolBar1(JToolBar toolBar) {
        JButton btnLogin = new JButton();
        btnLogin.setToolTipText("登录");
        btnLogin.setIcon(SmartIcons.signin);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final SmartClient client = getClient();
                boolean ok = true;
                if (client.isLogin()) {
                    ok = JOptionPane.showConfirmDialog(null,
                            "您已处于登录状态，确定要重新登录吗？") == 0;
                }
                if (ok) {
                    client.setLoginCallback(new DefaultLoginCallback() {
                        protected void onLoginFinish(boolean success,
                                Exception e) {
                            if (success) {
                                initContacts();
                            }
                            else {
                                LOG.error("登录失败", e);
                            }
                        };
                    });
                    new Thread() {
                        @Override
                        public void run() {
                            client.login();
                        }
                    }.start();
                }
                else {
                    initContacts();
                }
            }
        });
        toolBar.add(btnLogin);
        
        JButton btnClose = new JButton();
        btnClose.setToolTipText("关闭");
        btnClose.setIcon(SmartIcons.close);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        toolBar.add(btnClose);
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
    
    private Map<String, IMChatConsole> consoles = new HashMap<>();
    
    public IMChatConsole findConsole(IContact contact, boolean add) {
        return consoles.get(contact.getUin());
    }
    
    public void randBling() {
        int size = tabbedChat.getComponentCount();
        int i = new Random().nextInt(size);
        String name = "Random";
        if (i >= 0 && tabbedChat instanceof ClosableTabHost) {
            ((ClosableTabHost) tabbedChat).bling(i, name);
        }
    }
    
    public void highlight(IMChatConsole console) {
        int i = tabbedChat.indexOfComponent(console);
        if (i >= 0 && tabbedChat instanceof ClosableTabHost) {
            ((ClosableTabHost) tabbedChat).bling(i, console.getName());
        }
    }
    
    public IMChatConsole findConsoleById(String id, boolean show) {
        int count = tabbedChat.getTabCount();
        for (int i = 0; i < count; i++) {
            if (tabbedChat.getComponentAt(i) instanceof IMChatConsole) {
                IMChatConsole t = (IMChatConsole) tabbedChat.getComponentAt(i);
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
        if (client.isClose()) {
            LOG.sendNotification("错误", "连接已断开，请重新登录");
            return;
        }
        if (obj instanceof IContact) {
            openConsole((IContact) obj);
        }
    }
    
    public void openConsole(IContact contact) {
        IMChatConsole console = findConsoleById(contact.getUin(), true);
        if (console == null) {
            console = createConsoleUI(contact);
            tabbedChat.addTab(contact.getName(), console);
            consoles.put(console.getUin(), console);
            tabbedChat.setSelectedComponent(console);
        }
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
    }
    
    public void initContacts() {
        left.initContacts();
    }
    
    public void notifyUpdateContacts(int index, boolean force) {
        left.notifyUpdateContacts(index, force);
    }
}
