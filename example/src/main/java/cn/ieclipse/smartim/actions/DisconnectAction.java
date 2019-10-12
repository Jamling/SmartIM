package cn.ieclipse.smartim.actions;

import java.awt.event.ActionEvent;

import cn.ieclipse.smartim.views.IMPanel;
import icons.SmartIcons;

/**
 * Created by Jamling on 2017/7/12.
 */
public class DisconnectAction extends IMPanelAction {

    public DisconnectAction(IMPanel panel) {
        super(panel, SmartIcons.close);
        setToolTipText("断开连接并退出客户端");
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        panel.close();
    }
}
