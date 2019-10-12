package cn.ieclipse.wechat;

import javax.swing.JDialog;

import cn.ieclipse.smartim.actions.BroadcastAction;

public class WXBroadcastAction extends BroadcastAction {

    public WXBroadcastAction(WechatPanel panel) {
        super(panel);
    }

    @Override
    protected void openDialog() {
        WXBroadcastDialog dialog = new WXBroadcastDialog((WechatPanel)panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
