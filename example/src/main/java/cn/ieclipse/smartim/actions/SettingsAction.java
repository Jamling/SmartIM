package cn.ieclipse.smartim.actions;

import java.awt.event.ActionEvent;

import cn.ieclipse.smartim.common.Notifications;
import cn.ieclipse.smartim.dialogs.SettingsDialog;
import cn.ieclipse.smartim.views.IMPanel;
import icons.SmartIcons;

/**
 * Created by Jamling on 2017/7/12.
 */
public class SettingsAction extends IMPanelAction {

    public SettingsAction(IMPanel panel) {
        super(panel, SmartIcons.settings);
        setToolTipText("设置");
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        SettingsDialog.main(null);
    }
}
