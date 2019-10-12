package cn.ieclipse.smartim.console;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Created by Jamling on 2017/7/14.
 */
public class ChatHistoryPane extends JPanel {
    public ChatHistoryPane() {
        setLayout(new BorderLayout(0, 0));
        editorPane = new JEditorPane();
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);
    }

    private JPanel panel;
    private JEditorPane editorPane;

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JPanel getPanel() {
        return this;
    }
}
