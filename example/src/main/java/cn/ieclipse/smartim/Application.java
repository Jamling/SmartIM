package cn.ieclipse.smartim;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import cn.ieclipse.smartqq.SmartQQPanel;
import cn.ieclipse.wechat.WechatPanel;
import icons.SmartIcons;
import io.github.biezhi.wechat.api.WechatClient;

public class Application {
    
    private JFrame frmSmartim;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        WechatClient.initSSL();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Application window = new Application();
                    window.frmSmartim.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Create the application.
     */
    public Application() {
        initialize();
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmSmartim = new JFrame();
        frmSmartim.setTitle("SmartIM-SmartQQ/微信网页版");
        frmSmartim.setBounds(100, 100, 700, 450);
        frmSmartim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frmSmartim.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        SmartQQPanel panel = new SmartQQPanel();
        tabbedPane.addTab("SmartQQ", SmartIcons.qq, panel, null);
        
        WechatPanel panel2 = new WechatPanel();
        tabbedPane.addTab("Wechat", SmartIcons.wechat, panel2, null);
        
        // JMenuBar menuBar = new JMenuBar();
        // frmSmartim.setJMenuBar(menuBar);
        //
        // JMenu mnNewMenu = new JMenu("文件");
        // mnNewMenu.setMnemonic('F');
        // menuBar.add(mnNewMenu);
        //
        // JMenuItem mntmNewMenuItem = new JMenuItem("SmartQQ");
        // mnNewMenu.add(mntmNewMenuItem);
        //
        // JMenuItem mntmNewMenuItem_1 = new JMenuItem("微信");
        // mnNewMenu.add(mntmNewMenuItem_1);
    }
    
}
