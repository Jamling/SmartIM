package cn.ieclipse.smartim.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.smartim.settings.SmartIMSettings;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsDialog extends JDialog implements ActionListener {
    private JCheckBox chkNotify;
    private JCheckBox chkNotifyUnread;
    private JCheckBox chkSendBtn;
    private JPanel panel;
    private JCheckBox chkNotifyGroupMsg;
    private JCheckBox chkNotifyUnknown;
    private JCheckBox chkHideMyInput;
    private JLabel linkUpdate;
    private JLabel linkAbout;
    private SmartIMSettings settings;
    
    private String version = "2.1.2";
    private String update_url = "http://api.ieclipse.cn/smartqq/index/notice?p=swing";
    private String about_url = "http://api.ieclipse.cn/smartqq/index/about";
    private final JPanel contentPanel = new JPanel();
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            SettingsDialog dialog = new SettingsDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Create the dialog.
     */
    public SettingsDialog() {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, 1.0,
                Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            chkSendBtn = new JCheckBox("显示发送按键（MAC上无法按Enter发送？）");
            GridBagConstraints gbc_chkSend = new GridBagConstraints();
            gbc_chkSend.anchor = GridBagConstraints.WEST;
            gbc_chkSend.insets = new Insets(0, 0, 0, 0);
            gbc_chkSend.gridx = 0;
            gbc_chkSend.gridy = 0;
            contentPanel.add(chkSendBtn, gbc_chkSend);
        }
        {
            chkHideMyInput = new JCheckBox("自己输入的内容不显示到聊天记录");
            GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxNewCheckBox_1.gridx = 0;
            gbc_chckbxNewCheckBox_1.gridy = 1;
            contentPanel.add(chkHideMyInput, gbc_chckbxNewCheckBox_1);
        }
        {
            JLabel lblNewLabel = new JLabel("消息通知");
            GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
            gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel.gridx = 0;
            gbc_lblNewLabel.gridy = 2;
            contentPanel.add(lblNewLabel, gbc_lblNewLabel);
        }
        {
            chkNotify = new JCheckBox("接收消息时在右下角弹出通知框");
            chkNotify.setEnabled(false);
            GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_2.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_2.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxNewCheckBox_2.gridx = 0;
            gbc_chckbxNewCheckBox_2.gridy = 3;
            contentPanel.add(chkNotify, gbc_chckbxNewCheckBox_2);
        }
        {
            chkNotifyGroupMsg = new JCheckBox("群消息通知");
            chkNotifyGroupMsg.setEnabled(false);
            GridBagConstraints gbc_chckbxNewCheckBox_3 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_3.fill = GridBagConstraints.HORIZONTAL;
            gbc_chckbxNewCheckBox_3.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxNewCheckBox_3.gridx = 1;
            gbc_chckbxNewCheckBox_3.gridy = 3;
            contentPanel.add(chkNotifyGroupMsg, gbc_chckbxNewCheckBox_3);
        }
        {
            chkNotifyUnread = new JCheckBox("接收消息时更新未读数目");
            chkNotifyUnread.setEnabled(false);
            GridBagConstraints gbc_chckbxNewCheckBox_4 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_4.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_4.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxNewCheckBox_4.gridx = 0;
            gbc_chckbxNewCheckBox_4.gridy = 4;
            contentPanel.add(chkNotifyUnread, gbc_chckbxNewCheckBox_4);
        }
        {
            chkNotifyUnknown = new JCheckBox("未识别用户消息通知");
            chkNotifyUnknown.setEnabled(false);
            GridBagConstraints gbc_chckbxNewCheckBox_5 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_5.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_5.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxNewCheckBox_5.gridx = 1;
            gbc_chckbxNewCheckBox_5.gridy = 4;
            contentPanel.add(chkNotifyUnknown, gbc_chckbxNewCheckBox_5);
        }
        {
            linkUpdate = new JLabel("<html><a href=\"\">检查新版本</a></html>");
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
            gbc_lblNewLabel_1.gridx = 0;
            gbc_lblNewLabel_1.gridy = 7;
            contentPanel.add(linkUpdate, gbc_lblNewLabel_1);
        }
        {
            linkAbout = new JLabel("<html><a href=\"\">关于</a></html>");
            GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
            gbc_lblNewLabel_2.gridx = 1;
            gbc_lblNewLabel_2.gridy = 7;
            contentPanel.add(linkAbout, gbc_lblNewLabel_2);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("确定");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
                okButton.addActionListener(this);
            }
            {
                JButton cancelButton = new JButton("取消");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
                cancelButton.addActionListener(this);
            }
        }
        
        linkUpdate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                checkUpdate();
            }
        });
        linkAbout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cn.ieclipse.util.BareBonesBrowserLaunch.openURL(about_url);
            }
        });
        
        settings = SmartIMSettings.getInstance();
        settings.loadProp();
        reset();
        
    }
    
    public void reset() {
        chkNotify.setSelected(settings.getState().NOTIFY_MSG);
        chkNotifyGroupMsg.setSelected(settings.getState().NOTIFY_GROUP_MSG);
        chkSendBtn.setSelected(settings.getState().SHOW_SEND);
        chkNotifyUnread.setSelected(settings.getState().NOTIFY_UNREAD);
        chkNotifyUnknown.setSelected(settings.getState().NOTIFY_UNKNOWN);
        chkHideMyInput.setSelected(settings.getState().HIDE_MY_INPUT);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            settings.getState().NOTIFY_MSG = chkNotify.isSelected();
            settings.getState().NOTIFY_UNREAD = chkNotifyUnread.isSelected();
            settings.getState().SHOW_SEND = chkSendBtn.isSelected();
            settings.getState().NOTIFY_GROUP_MSG = chkNotifyGroupMsg
                    .isSelected();
            settings.getState().NOTIFY_UNKNOWN = chkNotifyUnknown.isSelected();
            settings.getState().HIDE_MY_INPUT = chkHideMyInput.isSelected();
            settings.saveProp();
        }
        else if ("Cancel".equals(e.getActionCommand())) {
        
        }
        dispose();
    }
    
    private void checkUpdate() {
        new Thread() {
            public void run() {
                try {
                    okhttp3.Request.Builder builder = (new okhttp3.Request.Builder())
                            .url(update_url).get();
                    Request request = builder.build();
                    Call call = new OkHttpClient().newCall(request);
                    Response response = call.execute();
                    String json = response.body().string();
                    LOG.info(json);
                    if (response.code() == 200) {
                        final UpdateInfo info = new Gson().fromJson(json,
                                UpdateInfo.class);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                String msg = String.format(
                                        "当前版本：%s\n最新版本：%s，是否下载？\n下载地址：%s",
                                        version, info.latest, info.link);
                                int ret = JOptionPane.showConfirmDialog(null,
                                        msg, "更新",
                                        JOptionPane.OK_CANCEL_OPTION);
                                if (ret == JOptionPane.OK_OPTION) {
                                    cn.ieclipse.util.BareBonesBrowserLaunch
                                            .openURL(info.link);
                                }
                            }
                        });
                    }
                } catch (Exception ex) {
                    LOG.error("检查SmartIM最新版本", ex);
                }
            };
        }.start();
    }
    
    public static class UpdateInfo {
        public String latest;
        public String desc;
        public String link;
    }
}
