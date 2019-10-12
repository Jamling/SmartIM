package cn.ieclipse.smartim.console;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

/**
 * Created by Jamling on 2017/7/14.
 */
public class ChatInputPane extends JPanel {
    public ChatInputPane() {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        textPane = new JTextPane();
        scrollPane.setViewportView(textPane);

        JPanel panel_1 = new JPanel();
        add(panel_1, BorderLayout.EAST);

        btnSend = new JButton("Send");
        panel_1.add(btnSend);
    }

    private JPanel panel;
    private JButton btnSend;
    private JTextPane textPane;

    public JTextPane getTextPane() {
        return textPane;
    }

    public JButton getBtnSend() {
        return btnSend;
    }

    public JPanel getPanel() {
        return this;
    }
}
