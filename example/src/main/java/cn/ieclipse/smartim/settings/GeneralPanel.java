package cn.ieclipse.smartim.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import cn.ieclipse.smartim.common.LOG;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeneralPanel extends JPanel {
    private JCheckBox chkNotify;
    private JCheckBox chkNotifyUnread;
    private JCheckBox chkSendBtn;
    private JCheckBox chkNotifyGroupMsg;
    private JCheckBox chkNotifyUnknown;
    private JCheckBox chkHideMyInput;
    private JLabel linkUpdate;
    private JLabel linkAbout;
    
    private SmartIMSettings settings;
    
    private String update_url = "http://api.ieclipse.cn/smartqq/index/notice?p=swing";
    private String about_url = "http://api.ieclipse.cn/smartqq/index/about";
    private String version = "2.2.0";
    private JCheckBox chkHistory;
    
    /**
     * Create the panel.
     */
    public GeneralPanel(SmartIMSettings settings) {
        this.settings = settings;
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, 1.0,
                Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 1.0 };
        JPanel contentPanel = this;
        contentPanel.setLayout(gbl_contentPanel);
        {
            chkSendBtn = new JCheckBox("显示发送按键（MAC上无法按Enter发送？）");
            GridBagConstraints gbc_chkSend = new GridBagConstraints();
            gbc_chkSend.anchor = GridBagConstraints.WEST;
            gbc_chkSend.insets = new Insets(0, 0, 5, 5);
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
            chkHistory = new JCheckBox("保存聊天记录");
            GridBagConstraints gbc_chkHistory = new GridBagConstraints();
            gbc_chkHistory.anchor = GridBagConstraints.WEST;
            gbc_chkHistory.insets = new Insets(0, 0, 5, 5);
            gbc_chkHistory.gridx = 0;
            gbc_chkHistory.gridy = 5;
            add(chkHistory, gbc_chkHistory);
        }
        {
            linkUpdate = new JLabel("<html><a href=\"\">检查新版本</a></html>");
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.insets = new Insets(0, 5, 5, 5);
            gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
            gbc_lblNewLabel_1.gridx = 0;
            gbc_lblNewLabel_1.gridy = 6;
            contentPanel.add(linkUpdate, gbc_lblNewLabel_1);
        }
        {
            linkAbout = new JLabel("<html><a href=\"\">关于</a></html>");
            GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
            gbc_lblNewLabel_2.insets = new Insets(0, 5, 5, 0);
            gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_2.fill = GridBagConstraints.VERTICAL;
            gbc_lblNewLabel_2.gridx = 1;
            gbc_lblNewLabel_2.gridy = 6;
            contentPanel.add(linkAbout, gbc_lblNewLabel_2);
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
    
    public void setSettings(SmartIMSettings settings) {
        this.settings = settings;
    }
    
    public void reset() {
        chkNotify.setSelected(settings.getState().NOTIFY_MSG);
        chkNotifyGroupMsg.setSelected(settings.getState().NOTIFY_GROUP_MSG);
        chkSendBtn.setSelected(settings.getState().SHOW_SEND);
        chkNotifyUnread.setSelected(settings.getState().NOTIFY_UNREAD);
        chkNotifyUnknown.setSelected(settings.getState().NOTIFY_UNKNOWN);
        chkHideMyInput.setSelected(settings.getState().HIDE_MY_INPUT);
        chkHistory.setSelected(settings.getState().LOG_HISTORY);
    }
    
    public void apply() {
        settings.getState().NOTIFY_MSG = chkNotify.isSelected();
        settings.getState().NOTIFY_UNREAD = chkNotifyUnread.isSelected();
        settings.getState().SHOW_SEND = chkSendBtn.isSelected();
        settings.getState().NOTIFY_GROUP_MSG = chkNotifyGroupMsg.isSelected();
        settings.getState().NOTIFY_UNKNOWN = chkNotifyUnknown.isSelected();
        settings.getState().HIDE_MY_INPUT = chkHideMyInput.isSelected();
        settings.getState().LOG_HISTORY = chkHistory.isSelected();
    }
}
