package cn.ieclipse.smartim.common;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.views.IMPanel;

/**
 * Created by Jamling on 2017/10/31.
 */
public class Notifications {
    public static void notify(final String title, final CharSequence text) {
        notify(null, null, title, text);
    }

    public static void notify(final IMPanel contactView, final IContact target, final String title,
        final CharSequence text) {
        if (text != null && title != null) {
            showPopup(title, text.toString(), contactView, target);
        }
    }

    public static void init(Window window) {
        Notifications.window = window;
        Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
        popupWidth = sd.width / 8;
        popupHeight = sd.height / 8;

        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(window.getGraphicsConfiguration());

        popupInsets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
        popupInsets.left = sd.width - popupWidth;
        popupInsets.top = sd.height - popupHeight - insets.bottom;
        popupInsets.right = sd.width;
        popupInsets.bottom = insets.bottom;
        popupBounds = new Rectangle(popupInsets.left, popupInsets.top, popupWidth, popupHeight);

        popupPanel = new PopupPanel(popupWidth, popupHeight);
        popupPanel.setBounds(popupBounds);
        popupPanel.setCallback(popupCallback);
    }

    private static Window window;
    private static int popupWidth = 160;
    private static int popupHeight = 80;
    private static Insets popupInsets;
    private static Rectangle popupBounds;
    private static PopupPanel popupPanel;
    private static Popup popup;
    private static PopupPanel.Callback popupCallback = new PopupPanel.Callback() {
        @Override
        public void close() {
            if (Notifications.popup != null) {
                Notifications.popup.hide();
                Notifications.popup = null;
            }
        }
    };

    public static void showPopup(String title, String message, final IMPanel contactView, final IContact target) {
        if (Notifications.popup == null) {
            if (popupPanel != null) {
                popupPanel.dispose();
            }
            popupPanel = new PopupPanel(popupWidth, popupHeight);
            popupPanel.setCallback(popupCallback);
            popupPanel.setMessage(title, message);
            popupPanel.setTarget(contactView, target);
            Popup popup = PopupFactory.getSharedInstance().getPopup(window, popupPanel, popupBounds.x, popupBounds.y);
            Notifications.popup = popup;
            popup.show();
            return;
        }
        if (popupPanel != null) {
            popupPanel.setMessage(title, message);
            popupPanel.setVisible(true);
            return;
        }
    }

}
