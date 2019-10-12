package cn.ieclipse.smartqq;

import javax.swing.JDialog;

import cn.ieclipse.smartim.actions.BroadcastAction;

public class QQBroadcastAction extends BroadcastAction {

    public QQBroadcastAction(SmartQQPanel panel) {
        super(panel);
    }

    @Override
    protected void openDialog() {
        QQBroadcastDialog dialog = new QQBroadcastDialog((SmartQQPanel)panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
