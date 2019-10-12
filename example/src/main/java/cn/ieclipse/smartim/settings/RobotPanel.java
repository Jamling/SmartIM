package cn.ieclipse.smartim.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RobotPanel extends JPanel {

    private JCheckBox chkRobot;
    private JLabel lblNewLabel_2;
    private JTextField textRobotName;
    private JLabel label;
    private JComboBox comboRobot;
    private JLabel lblNewLabel_3;
    private JTextField textApiKey;
    private JLabel lblNewLabel_4;
    private JTextField textWelcome;
    private JCheckBox chkGroupAny;
    private JCheckBox chkFriendAny;
    private JLabel lblNewLabel_5;
    private JTextField textReplyEmpty;
    private SmartIMSettings settings;

    /**
     * Create the panel.
     */
    public RobotPanel(SmartIMSettings settings) {
        this.settings = settings;
        JPanel panel_1 = this;
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] {0, 0, 0};
        gbl_panel_1.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panel_1.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel_1.setLayout(gbl_panel_1);
        {
            chkRobot = new JCheckBox("开启聊天机器人");
            GridBagConstraints gbc_chkRobot = new GridBagConstraints();
            gbc_chkRobot.insets = new Insets(0, 0, 5, 5);
            gbc_chkRobot.gridx = 0;
            gbc_chkRobot.gridy = 0;
            panel_1.add(chkRobot, gbc_chkRobot);
        }
        {
            lblNewLabel_2 = new JLabel("机器人名称");
            GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
            gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_2.gridx = 0;
            gbc_lblNewLabel_2.gridy = 1;
            panel_1.add(lblNewLabel_2, gbc_lblNewLabel_2);
        }
        {
            textRobotName = new JTextField();
            GridBagConstraints gbc_textRobotName = new GridBagConstraints();
            gbc_textRobotName.insets = new Insets(0, 0, 5, 0);
            gbc_textRobotName.fill = GridBagConstraints.HORIZONTAL;
            gbc_textRobotName.gridx = 1;
            gbc_textRobotName.gridy = 1;
            panel_1.add(textRobotName, gbc_textRobotName);
            textRobotName.setColumns(10);
        }
        {
            label = new JLabel("机器人");
            GridBagConstraints gbc_label = new GridBagConstraints();
            gbc_label.anchor = GridBagConstraints.WEST;
            gbc_label.insets = new Insets(0, 0, 5, 5);
            gbc_label.gridx = 0;
            gbc_label.gridy = 2;
            panel_1.add(label, gbc_label);
        }
        {
            comboRobot = new JComboBox();
            comboRobot.setModel(new DefaultComboBoxModel(new String[] {"图灵机器人"}));
            GridBagConstraints gbc_comboRobot = new GridBagConstraints();
            gbc_comboRobot.insets = new Insets(0, 0, 5, 0);
            gbc_comboRobot.fill = GridBagConstraints.HORIZONTAL;
            gbc_comboRobot.gridx = 1;
            gbc_comboRobot.gridy = 2;
            panel_1.add(comboRobot, gbc_comboRobot);
        }
        {
            lblNewLabel_3 = new JLabel("API key");
            GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
            gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel_3.gridx = 0;
            gbc_lblNewLabel_3.gridy = 3;
            panel_1.add(lblNewLabel_3, gbc_lblNewLabel_3);
        }
        {
            textApiKey = new JTextField();
            GridBagConstraints gbc_textApiKey = new GridBagConstraints();
            gbc_textApiKey.insets = new Insets(0, 0, 5, 0);
            gbc_textApiKey.fill = GridBagConstraints.HORIZONTAL;
            gbc_textApiKey.gridx = 1;
            gbc_textApiKey.gridy = 3;
            panel_1.add(textApiKey, gbc_textApiKey);
            textApiKey.setColumns(10);
        }
        {
            lblNewLabel_4 = new JLabel("群成员欢迎词");
            GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
            gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel_4.gridx = 0;
            gbc_lblNewLabel_4.gridy = 4;
            panel_1.add(lblNewLabel_4, gbc_lblNewLabel_4);
        }
        {
            textWelcome = new JTextField();
            textWelcome.setToolTipText("{user}和{memo}会被具体的成员名称和群公告替换");
            textWelcome.setText("欢迎{user} {memo}");
            GridBagConstraints gbc_textWelcome = new GridBagConstraints();
            gbc_textWelcome.insets = new Insets(0, 0, 5, 0);
            gbc_textWelcome.fill = GridBagConstraints.HORIZONTAL;
            gbc_textWelcome.gridx = 1;
            gbc_textWelcome.gridy = 4;
            panel_1.add(textWelcome, gbc_textWelcome);
            textWelcome.setColumns(10);
        }
        {
            chkGroupAny = new JCheckBox("回复群成员的任意消息（当聊天窗口打开时有效）");
            GridBagConstraints gbc_chkGroupAny = new GridBagConstraints();
            gbc_chkGroupAny.insets = new Insets(0, 0, 5, 0);
            gbc_chkGroupAny.anchor = GridBagConstraints.WEST;
            gbc_chkGroupAny.gridwidth = 2;
            gbc_chkGroupAny.gridx = 0;
            gbc_chkGroupAny.gridy = 5;
            panel_1.add(chkGroupAny, gbc_chkGroupAny);
        }
        {
            chkFriendAny = new JCheckBox("回复好友消息");
            GridBagConstraints gbc_chkFriendAny = new GridBagConstraints();
            gbc_chkFriendAny.insets = new Insets(0, 0, 5, 0);
            gbc_chkFriendAny.anchor = GridBagConstraints.WEST;
            gbc_chkFriendAny.gridwidth = 2;
            gbc_chkFriendAny.gridx = 0;
            gbc_chkFriendAny.gridy = 6;
            panel_1.add(chkFriendAny, gbc_chkFriendAny);
        }
        {
            lblNewLabel_5 = new JLabel("内容为空自动回复");
            GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
            gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
            gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
            gbc_lblNewLabel_5.gridx = 0;
            gbc_lblNewLabel_5.gridy = 7;
            panel_1.add(lblNewLabel_5, gbc_lblNewLabel_5);
        }
        {
            textReplyEmpty = new JTextField();
            textReplyEmpty.setToolTipText("未设置不回复");
            GridBagConstraints gbc_textReplayEmpty = new GridBagConstraints();
            gbc_textReplayEmpty.fill = GridBagConstraints.HORIZONTAL;
            gbc_textReplayEmpty.gridx = 1;
            gbc_textReplayEmpty.gridy = 7;
            panel_1.add(textReplyEmpty, gbc_textReplayEmpty);
            textReplyEmpty.setColumns(10);
        }
    }

    public void reset() {
        int idx = settings.getState().ROBOT_TYPE;
        if (idx >= 0 && idx < comboRobot.getItemCount()) {
            comboRobot.setSelectedIndex(idx);
        }
        chkRobot.setSelected(settings.getState().ROBOT_ENABLE);
        chkFriendAny.setSelected(settings.getState().ROBOT_FRIEND_ANY);
        chkGroupAny.setSelected(settings.getState().ROBOT_GROUP_ANY);
        textApiKey.setText(settings.getState().ROBOT_KEY);
        textReplyEmpty.setText(settings.getState().ROBOT_REPLY_EMPTY);
        textRobotName.setText(settings.getState().ROBOT_NAME);
        textWelcome.setText(settings.getState().ROBOT_GROUP_WELCOME);
    }

    public void apply() {
        settings.getState().ROBOT_TYPE = comboRobot.getSelectedIndex();
        settings.getState().ROBOT_ENABLE = chkRobot.isSelected();
        settings.getState().ROBOT_FRIEND_ANY = chkFriendAny.isSelected();
        settings.getState().ROBOT_GROUP_ANY = chkGroupAny.isSelected();
        settings.getState().ROBOT_KEY = textApiKey.getText().trim();
        settings.getState().ROBOT_REPLY_EMPTY = textReplyEmpty.getText().trim();
        settings.getState().ROBOT_NAME = textRobotName.getText().trim();
        settings.getState().ROBOT_GROUP_WELCOME = textWelcome.getText().trim();
    }
}
