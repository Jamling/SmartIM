package cn.ieclipse.wechat;

import cn.ieclipse.smartim.callback.ModificationCallback;
import cn.ieclipse.smartim.model.IContact;
import io.github.biezhi.wechat.model.Contact;

/**
 * Created by Jamling on 2017/11/1.
 */
public class WXModificationCallback implements ModificationCallback {
    protected WechatPanel imPanel;

    public WXModificationCallback(WechatPanel imPanel) {
        this.imPanel = imPanel;
    }

    @Override
    public void onContactChanged(IContact contact) {
        if (contact instanceof Contact || contact == null) {
            imPanel.notifyUpdateContacts(0, true);
        }
    }
}
