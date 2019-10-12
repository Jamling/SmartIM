package cn.ieclipse.smartim.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import cn.ieclipse.smartim.dialogs.SettingsDialog;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.views.IMPanel;

public class PopupPanel extends JPanel {
    private IMPanel fContactView;
    private IContact target;
    JLabel lblTitle;
    JLabel lblText;
    int width = 160;
    int height = 80;
    int expired = 5000;
    private Timer timer;

    /**
     * Create the panel.
     */
    public PopupPanel(int width, int height) {
        this.width = width;
        this.height = height;
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(null);
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));

        JPanel top = new JPanel();
        top.setBounds(5, 5, width - 10, 20);
        top.setLayout(new BorderLayout(0, 0));
        lblTitle = new JLabel("");
        top.add(lblTitle);

        JButton close = new JButton("×");
        // close.setPreferredSize(new Dimension(30, -1));
        // close.setBackground(SystemColor.inactiveCaption);
        close.setOpaque(false);
        close.setContentAreaFilled(false);
        close.setHorizontalAlignment(SwingConstants.RIGHT);
        close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer != null) {
                    timer.stop();
                }
                if (callback != null) {
                    callback.close();
                }
            }
        });
        top.add(close, BorderLayout.EAST);
        add(top);

        lblText = new JLabel("");
        lblText.setBounds(5, 25, width - 10, height - 50);
        add(lblText);

        JPanel bottom = new JPanel();
        bottom.setBounds(5, height - 25, width - 10, 20);
        bottom.setLayout(new BorderLayout(0, 0));
        add(bottom);

        final JLabel lblSettings = new JLabel("<html><a href=\"\">提醒设置</a></html>");
        bottom.add(lblSettings, BorderLayout.WEST);

        final JLabel lblDetail = new JLabel("<html><a href=\"\">查看详情</a></html>");
        bottom.add(lblDetail, BorderLayout.EAST);

        lblSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (lblDetail == e.getComponent()) {
                    if (target != null && fContactView != null) {
                        fContactView.openConsole(target);
                        fContactView.requestFocus();
                    }
                } else if (lblSettings == e.getSource()) {
                    SettingsDialog.main(null);
                }
            }
        });
        lblDetail.addMouseListener(lblSettings.getMouseListeners()[0]);

        timer = new Timer(expired, close.getActionListeners()[0]);
    }

    void setLabelText(JLabel label, String text) {
        StringBuilder builder = new StringBuilder("<html>");
        char[] chars = text.toCharArray();
        FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
        int start = 0;
        int len = 0;
        while (start + len < text.length()) {
            while (true) {
                len++;
                if (start + len > text.length())
                    break;
                if (fontMetrics.charsWidth(chars, start, len) > label.getWidth()) {
                    break;
                }
            }
            builder.append(chars, start, len - 1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(chars, start, text.length() - start);
        builder.append("</html>");
        label.setText(builder.toString());
    }

    public PopupPanel setTarget(IMPanel contactView, IContact target) {
        this.fContactView = contactView;
        this.target = target;
        return this;
    }

    public PopupPanel setMessage(final String title, final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLabelText(lblText, msg);
                lblTitle.setText(title);
                if (timer.isRunning()) {
                    timer.stop();
                }
                timer.setDelay(expired);
                timer.start();
            }
        });
        return this;
    }

    Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void close();
    }

    public void dispose() {
        if (timer != null) {
            timer.stop();
            setCallback(null);
        }
        removeAll();
    }
}
