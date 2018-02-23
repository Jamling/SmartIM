package cn.ieclipse.smartim.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import cn.ieclipse.smartim.IMHistoryManager;
import cn.ieclipse.smartim.SmartClient;
import cn.ieclipse.smartim.actions.ScrollLockAction;
import cn.ieclipse.smartim.actions.SendFileAction;
import cn.ieclipse.smartim.actions.SendImageAction;
import cn.ieclipse.smartim.common.IMUtils;
import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.smartim.common.WrapHTMLFactory;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.impl.AbstractContact;
import cn.ieclipse.smartim.settings.SmartIMSettings;
import cn.ieclipse.smartim.views.IMPanel;
import cn.ieclipse.util.BareBonesBrowserLaunch;
import cn.ieclipse.util.StringUtils;

/**
 * Created by Jamling on 2017/7/1.
 */
public abstract class IMChatConsole extends JPanel {
    
    public static final String ENTER_KEY = "\n";
    protected IContact contact;
    protected String uin;
    protected IMPanel imPanel;
    
    public IMChatConsole(IContact target, IMPanel imPanel) {
        this.contact = target;
        this.imPanel = imPanel;
        this.uin = target.getUin();
        initUI();
        loadHistories();
    }
    
    public SmartClient getClient() {
        return imPanel.getClient();
    }
    
    public abstract void loadHistory(String raw);
    
    public abstract void post(final String msg);
    
    public String getHistoryFile() {
        return uin;
    }
    
    public String getUin() {
        return uin;
    }
    
    public String trimMsg(String msg) {
        if (msg.endsWith(ENTER_KEY)) {
            return msg;
        }
        return msg + ENTER_KEY;
    }
    
    public void loadHistories() {
        SmartClient client = getClient();
        if (client != null) {
            List<String> ms = IMHistoryManager.getInstance().load(client,
                    getHistoryFile());
            for (String raw : ms) {
                if (!IMUtils.isEmpty(raw)) {
                    try {
                        loadHistory(raw);
                    } catch (Exception e) {
                        error("历史消息记录：" + raw);
                    }
                }
            }
        }
    }
    
    public void clearHistories() {
        IMHistoryManager.getInstance().clear(getClient(), getHistoryFile());
        //composite.clearHistory();
    }
    
    public void clearUnread() {
        if (contact != null && contact instanceof AbstractContact) {
            ((AbstractContact) contact).clearUnRead();
        }
    }
    
    public boolean hideMyInput() {
        return false;
    }
    
    public boolean checkClient(SmartClient client) {
        if (client == null || client.isClose()) {
            error("连接已关闭");
            return false;
        }
        if (!client.isLogin()) {
            error("请先登录");
            return false;
        }
        return true;
    }
    
    public void send(final String input) {
        SmartClient client = getClient();
        if (!checkClient(client)) {
            return;
        }
        String name = client.getAccount().getName();
        String msg = IMUtils.formatHtmlMyMsg(System.currentTimeMillis(), name,
                input);
        if (!hideMyInput()) {
            insertDocument(msg);
            IMHistoryManager.getInstance().save(client, getUin(), msg);
        }
        new Thread() {
            @Override
            public void run() {
                post(input);
            }
        }.start();
    }
    
    public void sendFile(final String file) {
        new Thread() {
            public void run() {
                uploadLock = true;
                try {
                    sendFileInternal(file);
                } catch (Exception e) {
                    LOG.error("发送文件失败 : " + e);
                    LOG.sendNotification("发送文件失败",
                            String.format("文件：%s(%s)", file, e.getMessage()));
                    error(String.format("发送文件失败：%s(%s)", file));
                } finally {
                    uploadLock = false;
                }
            }
        }.start();
    }
    
    protected void sendFileInternal(final String file) throws Exception{
    
    }
    
    public void error(Throwable e) {
        error(e == null ? "null" : e.toString());
    }
    
    public void error(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                insertDocument(
                        String.format("<div class=\"error\">%s</div>", msg));
            }
        });
    }
    
    private void createUIComponents() {
    
    }
    
    public void write(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                insertDocument(msg);
            }
        });
    }
    
    protected ChatHistoryPane top;
    protected ChatInputPane bottom;
    protected JEditorPane historyWidget;
    protected JTextPane inputWidget;
    protected JButton btnSend;
    protected JSplitPane spliter;
    
    public void initUI() {
        spliter = new JSplitPane();
        spliter.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOrientation(SwingConstants.VERTICAL);
        initToolBar(toolBar);
        add(toolBar, BorderLayout.WEST);
        
        add(spliter, BorderLayout.CENTER);
        
        top = new ChatHistoryPane();
        bottom = new ChatInputPane();
        historyWidget = top.getEditorPane();
        inputWidget = bottom.getTextPane();
        btnSend = bottom.getBtnSend();
        btnSend.setVisible(SmartIMSettings.getInstance().getState().SHOW_SEND);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputWidget.getText();
                if (!input.isEmpty()) {
                    inputWidget.setText("");
                    send(input);
                }
            }
        });
        
        top.getPanel().setPreferredSize(new Dimension(-1, 20));
        top.getPanel().setMinimumSize(new Dimension(-1, 20));
        spliter.setLeftComponent(top.getPanel());
        spliter.setRightComponent(bottom.getPanel());
        spliter.setResizeWeight(1.0f);
        
        inputWidget.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = inputWidget.getText();
                    if (!input.isEmpty()) {
                        inputWidget.setText("");
                        send(input);
                    }
                    e.consume();
                }
            }
        });
        initHistoryWidget();
    }
    
    protected void initToolBar(JToolBar toolBar) {
        toolBar.add(new SendImageAction(this));
        toolBar.add(new SendFileAction(this));
        toolBar.add(new ScrollLockAction(this));
    }
    
    protected boolean scrollLock = false;
    protected boolean uploadLock = false;
    
    public boolean enableUpload() {
        return !uploadLock;
    }
    
    public void setScrollLock(boolean scrollLock) {
        this.scrollLock = scrollLock;
    }
    
    protected void initHistoryWidget() {
        HTMLEditorKit kit = new HTMLEditorKit() {
            @Override
            public ViewFactory getViewFactory() {
                return new WrapHTMLFactory();
            }
        };
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {text-align: left;}");
        styleSheet.addRule("div.my {font-size: 1.2rem; font-style: italic;}");
        styleSheet.addRule("div.error {color: red;}");
        styleSheet.addRule("img {max-width: 100%; display: block;}");
        try {
            styleSheet.importStyleSheet(
                    new URL("http://dl.ieclipse.cn/r/smartim-min.css"));
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
        String initText = String.format(
                "<html><head></head><body>%s</body></html>", "欢迎使用SmartIM");
        historyWidget.setEditorKit(kit);
        historyWidget.setDocument(doc);
        // historyWidget.setText(initText);
        historyWidget.setEditable(false);
        historyWidget.addHyperlinkListener(new HyperlinkListener() {
            
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String desc = e.getDescription();
                    if (!StringUtils.isEmpty(desc)) {
                        hyperlinkActivated(desc);
                    }
                }
            }
        });
    }
    
    protected boolean hyperlinkActivated(String desc) {
        if (desc.startsWith("user://")) {
            String user = desc.substring(7);
            try {
                inputWidget.getDocument().insertString(
                        inputWidget.getCaretPosition(), "@" + user + " ", null);
            } catch (Exception e) {
            
            }
        }
        else if (desc.startsWith("code://")) {
            String code = desc.substring(7);
            int pos = code.lastIndexOf(':');
            String file = code.substring(0, pos);
            int line = Integer.parseInt(code.substring(pos + 1).trim());
            if (line > 0) {
                line--;
            }
            // TODO open file in editor and located to line
        }
        else {
            BareBonesBrowserLaunch.openURL(desc);
        }
        return false;
    }
    
    protected void insertDocument(String msg) {
        try {
            HTMLEditorKit kit = (HTMLEditorKit) historyWidget.getEditorKit();
            HTMLDocument doc = (HTMLDocument) historyWidget.getDocument();
            // historyWidget.getDocument().insertString(len - offset,
            // trimMsg(msg), null);
            // Element root = doc.getDefaultRootElement();
            // Element body = root.getElement(1);
            // doc.insertBeforeEnd(body, msg);
            int pos = historyWidget.getCaretPosition();
            kit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            historyWidget.setCaretPosition(scrollLock ? pos : doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
