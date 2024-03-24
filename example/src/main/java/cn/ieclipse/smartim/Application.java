package cn.ieclipse.smartim;

import cn.ieclipse.smartim.common.Notifications;
import cn.ieclipse.smartim.common.RestUtils;
import cn.ieclipse.smartim.common.SwingUtils;
import cn.ieclipse.smartim.settings.SmartIMSettings;
import cn.ieclipse.smartqq.SmartQQPanel;
import cn.ieclipse.wechat.WechatPanel;
import icons.SmartIcons;
import io.github.biezhi.wechat.api.WechatClient;

import javax.swing.*;
import java.awt.*;

public class Application {

    private JFrame window;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        WechatClient.initSSL();
        SmartIMSettings.getInstance().loadProp();
        EventQueue.invokeLater(() -> {
            try {
                SwingUtils.initLookAndFeel();
                Application window = new Application();
                window.window.setVisible(true);
                Notifications.init(window.window);
            } catch (Exception e) {
                e.printStackTrace();
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
        window = new JFrame() {
            @Override
            public void dispose() {
                IMHistoryManager.getInstance().flush();
                super.dispose();
            }
        };
        window.setTitle(String.format("SmartIM (%s)", RestUtils.VERSION));
        window.setBounds(100, 100, 700, 450);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        window.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        WechatPanel panel2 = new WechatPanel();
        tabbedPane.addTab("Wechat", SmartIcons.wechat, panel2, null);

        SmartQQPanel panel1 = new SmartQQPanel();
        tabbedPane.addTab("SmartQQ", SmartIcons.qq, panel1, null);
    }
}
