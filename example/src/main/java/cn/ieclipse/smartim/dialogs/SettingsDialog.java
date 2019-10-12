package cn.ieclipse.smartim.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import cn.ieclipse.smartim.settings.GeneralPanel;
import cn.ieclipse.smartim.settings.RobotPanel;
import cn.ieclipse.smartim.settings.SmartIMSettings;
import cn.ieclipse.smartim.settings.UploadPanel;

public class SettingsDialog extends JDialog implements ActionListener {
    private SmartIMSettings settings;
    private GeneralPanel generalPanel;
    private RobotPanel robotPanel;
    private UploadPanel uploadPanel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            SettingsDialog dialog = new SettingsDialog();
            dialog.setTitle("设置");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public SettingsDialog() {
        setSize(500, 450);
        getContentPane().setLayout(new BorderLayout());
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
            settings = SmartIMSettings.getInstance();
            settings.loadProp();
            {
                JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
                getContentPane().add(tabbedPane, BorderLayout.CENTER);
                {
                    generalPanel = new GeneralPanel(settings);
                    JScrollPane scrollPane = new JScrollPane(generalPanel);
                    tabbedPane.addTab("常规", null, scrollPane, null);

                    robotPanel = new RobotPanel(settings);
                    scrollPane = new JScrollPane(robotPanel);
                    tabbedPane.addTab("机器人", null, scrollPane, null);

                    uploadPanel = new UploadPanel(settings);
                    scrollPane = new JScrollPane(uploadPanel);
                    tabbedPane.addTab("文件传输", null, scrollPane, null);
                }
            }
        }
        reset();
    }

    public void reset() {
        if (generalPanel != null) {
            generalPanel.reset();
        }
        if (robotPanel != null) {
            robotPanel.reset();
        }
        if (uploadPanel != null) {
            uploadPanel.reset();
        }
    }

    public void apply() {
        if (generalPanel != null) {
            generalPanel.apply();
        }
        if (robotPanel != null) {
            robotPanel.apply();
        }
        if (uploadPanel != null) {
            uploadPanel.apply();
        }
        settings.saveProp();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            apply();
        } else if ("Cancel".equals(e.getActionCommand())) {

        }
        dispose();
    }
}
