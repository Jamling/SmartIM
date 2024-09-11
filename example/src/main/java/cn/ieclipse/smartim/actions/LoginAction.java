package cn.ieclipse.smartim.actions;

import cn.ieclipse.smartim.SmartClient;
import cn.ieclipse.smartim.callback.impl.DefaultLoginCallback;
import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.smartim.settings.SmartIMSettings;
import cn.ieclipse.smartim.views.IMPanel;
import icons.SmartIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Jamling on 2017/7/12.
 */
public class LoginAction extends IMPanelAction {

    public LoginAction(IMPanel panel) {
        super(panel, SmartIcons.signin);
        setToolTipText("登录");
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        final SmartClient client = panel.getClient();
        boolean ok = true;
        if (client.isLogin()) {
            ok = JOptionPane.showConfirmDialog(null, "您已处于登录状态，确定要重新登录吗？") == 0;
        }
        if (ok) {
            client.setAutoLogin(SmartIMSettings.getInstance().getState().AUTO_LOGIN);
            client.setLoginCallback(new DefaultLoginCallback() {
                protected void onLoginFinish(boolean success, Exception e) {
                    if (success) {
                        panel.initContacts();
                    } else {
                        LOG.error("登录失败", e);
                    }
                };
            });
            new Thread(() -> client.login()).start();
        } else {
            panel.initContacts();
        }
    }
}
