package cn.ieclipse.smartim.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.ieclipse.smartim.SmartClient;
import cn.ieclipse.smartim.views.IMPanel;
import icons.SmartIcons;

/**
 * Created by Jamling on 2018/02/23.
 */
public class BroadcastAction extends IMPanelAction {

    public BroadcastAction(IMPanel panel) {
        super(panel, SmartIcons.broadcast);
        setToolTipText("消息群发");
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        final SmartClient client = panel.getClient();
        if (client.isLogin()) {
            openDialog();
        } else {
            JOptionPane.showMessageDialog(null, "已断开连接，请重新登录成功后再试");
        }
    }

    protected void openDialog() {

    }

    public static String groupMacro = "{group}";
    public static String memberMacro = "{member}";
}
