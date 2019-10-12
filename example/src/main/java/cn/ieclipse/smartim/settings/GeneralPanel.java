package cn.ieclipse.smartim.settings;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import cn.ieclipse.common.BareBonesBrowserLaunch;
import cn.ieclipse.smartim.IMClientFactory;
import cn.ieclipse.smartim.common.RestUtils;
import cn.ieclipse.smartim.common.SwingUtils;

public class GeneralPanel extends JPanel {
    private JCheckBox chkNotify;
    private JCheckBox chkNotifyUnread;
    private JCheckBox chkSendBtn;
    private JCheckBox chkNotifyFriendMsg;
    private JCheckBox chkNotifyGroupMsg;
    private JCheckBox chkNotifyUnknown;
    private JCheckBox chkHideMyInput;
    private JLabel linkUpdate;
    private JLabel linkAbout;

    private SmartIMSettings settings;
    private JCheckBox chkHistory;
    private JPanel panel;
    private JLabel lblNewLabel_1;
    private JTextField tfWorkDir;
    private JButton btnNewButton;
    private JPanel panel_1;
    private JComboBox<LookAndFeelInfo> comboTheme;
    private JPanel panel_2;
    private JLabel lblNewLabel_2;
    private JComboBox<String> comboSend;

    /**
     * Create the panel.
     */
    public GeneralPanel(SmartIMSettings settings) {
        this.settings = settings;
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] {0, 0, 0};
        gbl_contentPanel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0};
        JPanel contentPanel = this;
        contentPanel.setLayout(gbl_contentPanel);
        {
            panel_2 = new JPanel();
            GridBagConstraints gbc_panel_2 = new GridBagConstraints();
            gbc_panel_2.gridwidth = 2;
            gbc_panel_2.insets = new Insets(5, 5, 5, 5);
            gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
            gbc_panel_2.gridx = 0;
            gbc_panel_2.gridy = 0;
            add(panel_2, gbc_panel_2);
            panel_2.setLayout(new BorderLayout(0, 0));
            {
                lblNewLabel_2 = new JLabel("消息消息");
                panel_2.add(lblNewLabel_2, BorderLayout.WEST);
            }
            {
                comboSend = new JComboBox<>();
                comboSend.setModel(new DefaultComboBoxModel<>(new String[] {"Ctrl + Enter", "Enter"}));
                panel_2.add(comboSend, BorderLayout.CENTER);
            }
        }
        {
            chkSendBtn = new JCheckBox("显示发送按键（MAC上无法按Enter发送？）");
            GridBagConstraints gbc_chkSend = new GridBagConstraints();
            gbc_chkSend.anchor = GridBagConstraints.WEST;
            gbc_chkSend.insets = new Insets(0, 0, 5, 5);
            gbc_chkSend.gridx = 0;
            gbc_chkSend.gridy = 1;
            contentPanel.add(chkSendBtn, gbc_chkSend);
        }
        {
            chkHideMyInput = new JCheckBox("自己输入的内容不显示到聊天记录");
            GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxNewCheckBox_1.gridx = 0;
            gbc_chckbxNewCheckBox_1.gridy = 2;
            contentPanel.add(chkHideMyInput, gbc_chckbxNewCheckBox_1);
        }
        {
            JLabel lblNewLabel = new JLabel("消息通知");
            GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
            gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
            gbc_lblNewLabel.gridx = 0;
            gbc_lblNewLabel.gridy = 3;
            contentPanel.add(lblNewLabel, gbc_lblNewLabel);
        }
        {
            chkNotify = new JCheckBox("接收消息时在右下角弹出通知框");
            chkNotify.setEnabled(true);
            GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_2.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_2.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxNewCheckBox_2.gridx = 0;
            gbc_chckbxNewCheckBox_2.gridy = 4;
            contentPanel.add(chkNotify, gbc_chckbxNewCheckBox_2);
        }
        {
            chkNotifyGroupMsg = new JCheckBox("群消息通知");
            chkNotifyGroupMsg.setEnabled(true);
            GridBagConstraints gbc_chckbxNewCheckBox_3 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_3.fill = GridBagConstraints.HORIZONTAL;
            gbc_chckbxNewCheckBox_3.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxNewCheckBox_3.gridx = 1;
            gbc_chckbxNewCheckBox_3.gridy = 4;
            contentPanel.add(chkNotifyGroupMsg, gbc_chckbxNewCheckBox_3);
        }
        {
            chkNotifyUnread = new JCheckBox("接收消息时更新未读数目");
            chkNotifyUnread.setEnabled(true);
            GridBagConstraints gbc_chckbxNewCheckBox_4 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_4.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_4.insets = new Insets(0, 0, 5, 5);
            gbc_chckbxNewCheckBox_4.gridx = 0;
            gbc_chckbxNewCheckBox_4.gridy = 5;
            contentPanel.add(chkNotifyUnread, gbc_chckbxNewCheckBox_4);
        }
        {
            chkNotifyUnknown = new JCheckBox("未识别用户消息通知");
            chkNotifyUnknown.setEnabled(true);
            GridBagConstraints gbc_chckbxNewCheckBox_5 = new GridBagConstraints();
            gbc_chckbxNewCheckBox_5.anchor = GridBagConstraints.WEST;
            gbc_chckbxNewCheckBox_5.insets = new Insets(0, 0, 5, 0);
            gbc_chckbxNewCheckBox_5.gridx = 1;
            gbc_chckbxNewCheckBox_5.gridy = 5;
            contentPanel.add(chkNotifyUnknown, gbc_chckbxNewCheckBox_5);
        }
        {
            chkHistory = new JCheckBox("保存聊天记录");
            GridBagConstraints gbc_chkHistory = new GridBagConstraints();
            gbc_chkHistory.anchor = GridBagConstraints.WEST;
            gbc_chkHistory.insets = new Insets(0, 0, 5, 5);
            gbc_chkHistory.gridx = 0;
            gbc_chkHistory.gridy = 6;
            add(chkHistory, gbc_chkHistory);
        }
        {
            chkNotifyFriendMsg = new JCheckBox("好友消息通知");
            GridBagConstraints gbc_chkHistory = new GridBagConstraints();
            gbc_chkHistory.anchor = GridBagConstraints.WEST;
            gbc_chkHistory.insets = new Insets(0, 0, 5, 0);
            gbc_chkHistory.gridx = 1;
            gbc_chkHistory.gridy = 6;
            add(chkNotifyFriendMsg, gbc_chkHistory);
        }
        {
            panel = new JPanel();
            GridBagConstraints gbc_panel = new GridBagConstraints();
            gbc_panel.gridwidth = 2;
            gbc_panel.insets = new Insets(5, 5, 5, 0);
            gbc_panel.fill = GridBagConstraints.HORIZONTAL;
            gbc_panel.gridx = 0;
            gbc_panel.gridy = 7;
            add(panel, gbc_panel);
            GridBagLayout gbl_panel = new GridBagLayout();
            gbl_panel.columnWidths = new int[] {10, 20, 0, 10};
            gbl_panel.rowHeights = new int[] {15, 0};
            gbl_panel.columnWeights = new double[] {0.0, 1.0, 0.0, Double.MIN_VALUE};
            gbl_panel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
            panel.setLayout(gbl_panel);
            {
                lblNewLabel_1 = new JLabel("工作目录");
                GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
                gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
                gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
                gbc_lblNewLabel_1.gridx = 0;
                gbc_lblNewLabel_1.gridy = 0;
                panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
            }
            {
                tfWorkDir = new JTextField();
                tfWorkDir.setToolTipText("请设置二维码，聊天记录保存位置");
                GridBagConstraints gbc_tfWorkDir = new GridBagConstraints();
                gbc_tfWorkDir.insets = new Insets(0, 0, 0, 5);
                gbc_tfWorkDir.fill = GridBagConstraints.HORIZONTAL;
                gbc_tfWorkDir.gridx = 1;
                gbc_tfWorkDir.gridy = 0;
                panel.add(tfWorkDir, gbc_tfWorkDir);
                tfWorkDir.setColumns(10);
            }
            {
                btnNewButton = new JButton("浏览");
                btnNewButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser chooser = new JFileChooser(SmartIMSettings.getInstance().getState().WORK_PATH);
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int code = chooser.showOpenDialog(null);
                        if (code == JFileChooser.APPROVE_OPTION) {
                            if (chooser.getSelectedFile() != null) {
                                tfWorkDir.setText(chooser.getSelectedFile().getAbsolutePath());
                            }
                        }
                    }
                });
                GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
                gbc_btnNewButton.gridx = 2;
                gbc_btnNewButton.gridy = 0;
                panel.add(btnNewButton, gbc_btnNewButton);
            }
        }
        {
            panel_1 = new JPanel();
            GridBagConstraints gbc_panel_1 = new GridBagConstraints();
            gbc_panel_1.gridwidth = 2;
            gbc_panel_1.insets = new Insets(5, 5, 5, 0);
            gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
            gbc_panel_1.gridx = 0;
            gbc_panel_1.gridy = 8;
            add(panel_1, gbc_panel_1);
            panel_1.setLayout(new BorderLayout(0, 0));
            panel_1.add(new JLabel("主题"), BorderLayout.WEST);
            comboTheme = new JComboBox<>();
            comboTheme.setModel(new DefaultComboBoxModel<>(UIManager.getInstalledLookAndFeels()));
            comboTheme.setRenderer(new BasicComboBoxRenderer() {
                public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                    java.awt.Component c =
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof LookAndFeelInfo) {
                        setText(((LookAndFeelInfo)value).getName());
                    }
                    return c;
                };
            });
            panel_1.add(comboTheme, BorderLayout.CENTER);
        }
        {
            linkUpdate = new JLabel("<html><a href=\"\">检查新版本</a></html>");
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.insets = new Insets(0, 5, 5, 5);
            gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
            gbc_lblNewLabel_1.gridx = 0;
            gbc_lblNewLabel_1.gridy = 9;
            contentPanel.add(linkUpdate, gbc_lblNewLabel_1);
        }
        {
            linkAbout = new JLabel("<html><a href=\"\">关于</a></html>");
            GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
            gbc_lblNewLabel_2.insets = new Insets(0, 5, 5, 0);
            gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_2.fill = GridBagConstraints.VERTICAL;
            gbc_lblNewLabel_2.gridx = 1;
            gbc_lblNewLabel_2.gridy = 9;
            contentPanel.add(linkAbout, gbc_lblNewLabel_2);
        }

        linkUpdate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                RestUtils.checkUpdate();
            }
        });
        linkAbout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BareBonesBrowserLaunch.openURL(RestUtils.about_url);
            }
        });
    }

    public void setSettings(SmartIMSettings settings) {
        this.settings = settings;
    }

    public void reset() {
        chkNotify.setSelected(settings.getState().NOTIFY_MSG);
        chkNotifyFriendMsg.setSelected(settings.getState().NOTIFY_FRIEND_MSG);
        chkNotifyGroupMsg.setSelected(settings.getState().NOTIFY_GROUP_MSG);
        chkSendBtn.setSelected(settings.getState().SHOW_SEND);
        chkNotifyUnread.setSelected(settings.getState().NOTIFY_UNREAD);
        chkNotifyUnknown.setSelected(settings.getState().NOTIFY_UNKNOWN);
        chkHideMyInput.setSelected(settings.getState().HIDE_MY_INPUT);
        chkHistory.setSelected(settings.getState().LOG_HISTORY);
        tfWorkDir.setText(settings.getState().WORK_PATH);
        comboTheme.setSelectedIndex(settings.getState().THEME);
        // DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>)
        // comboSend.getModel();
        comboSend.setSelectedItem(settings.getState().KEY_SEND);
    }

    public void apply() {
        settings.getState().NOTIFY_MSG = chkNotify.isSelected();
        settings.getState().NOTIFY_UNREAD = chkNotifyUnread.isSelected();
        settings.getState().SHOW_SEND = chkSendBtn.isSelected();
        settings.getState().NOTIFY_FRIEND_MSG = chkNotifyFriendMsg.isSelected();
        settings.getState().NOTIFY_GROUP_MSG = chkNotifyGroupMsg.isSelected();
        settings.getState().NOTIFY_UNKNOWN = chkNotifyUnknown.isSelected();
        settings.getState().HIDE_MY_INPUT = chkHideMyInput.isSelected();
        settings.getState().LOG_HISTORY = chkHistory.isSelected();
        if (!settings.getState().WORK_PATH.equals(tfWorkDir.getText())) {
            IMClientFactory.getInstance().setWorkDir(tfWorkDir.getText());
        }
        settings.getState().WORK_PATH = tfWorkDir.getText();
        if (settings.getState().THEME != comboTheme.getSelectedIndex()) {
            SwingUtils.setLookAndFeel(comboTheme.getSelectedIndex());
            JOptionPane.showMessageDialog(null, "您已修改主题，建议重启本应用");
        }
        settings.getState().THEME = comboTheme.getSelectedIndex();
        settings.getState().KEY_SEND = comboSend.getSelectedItem().toString();
    }
}
