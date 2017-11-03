package cn.ieclipse.smartim.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import cn.ieclipse.smartim.AbstractSmartClient;
import cn.ieclipse.smartim.IMHistoryManager;
import cn.ieclipse.smartim.SmartClient;
import cn.ieclipse.smartim.common.IMUtils;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.views.IMPanel;
import icons.SmartIcons;

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
    
    public void loadHistories() {
        SmartClient client = getClient();
        if (client != null && client instanceof AbstractSmartClient) {
            List<String> ms = IMHistoryManager.getInstance()
                    .load((AbstractSmartClient) client, getHistoryFile());
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
    
    public void send(final String input) {
        SmartClient client = getClient();
        if (client == null || client.isClose()) {
            error("连接已关闭");
            return;
        }
        if (!client.isLogin()) {
            error("连接已关闭，请重新登录");
            return;
        }
        String name = getClient().getAccount().getName();
        String msg = IMUtils.formatMsg(System.currentTimeMillis(), name, input);
        // if (contact instanceof Friend)
        {
            try {
                historyWidget.getDocument().insertString(
                        historyWidget.getDocument().getLength(),
                        msg + ENTER_KEY, null);
                // historyWidget.setCaretPosition(historyWidget.getDocument().getEndPosition().getOffset());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        new Thread() {
            @Override
            public void run() {
                post(input);
            }
        }.start();
    }
    
    public void sendFile(final String file) {
    }
    
    public void error(Throwable e) {
        // e.printStackTrace(new PrintStream(errorStream));
    }
    
    public void error(String msg) {
        try {
            historyWidget.getDocument().insertString(
                    historyWidget.getDocument().getLength(),
                    msg + ENTER_KEY, null);
            // historyWidget.setCaretPosition(historyWidget.getDocument().getEndPosition().getOffset());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void createUIComponents() {
    
    }
    
    public void write(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    historyWidget.getDocument().insertString(
                            historyWidget.getDocument().getLength(),
                            msg + ENTER_KEY, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        btnSend.setVisible(true);
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
    }
    
    protected void initToolBar(JToolBar toolBar) {
        JButton btnFile = new JButton();
        btnFile.setToolTipText("发送文件");
        btnFile.setIcon(SmartIcons.file);
        btnFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.showDialog(new JLabel(), "选择要发送的文件");
                File f = chooser.getSelectedFile();
                if (f != null) {
                    sendFile(f.getAbsolutePath());
                }
            }
        });
        toolBar.add(btnFile);
    }
}
