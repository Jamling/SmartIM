package cn.ieclipse.wechat;

import javax.swing.JToolBar;

import cn.ieclipse.smartim.IMClientFactory;
import cn.ieclipse.smartim.console.IMChatConsole;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.views.IMContactView;
import cn.ieclipse.smartim.views.IMPanel;
import io.github.biezhi.wechat.api.WechatClient;

/**
 * Created by Jamling on 2017/11/1.
 */
public class WechatPanel extends IMPanel {
    public WechatPanel() {
        super();
    }

    @Override
    public WechatClient getClient() {
        return IMClientFactory.getInstance().getWechatClient();
    }

    @Override
    public IMContactView createContactsUI() {
        return new WXContactView(this);
    }

    @Override
    public IMChatConsole createConsoleUI(IContact contact) {
        return new WXChatConsole(contact, this);
    }
    
    @Override
    protected void initToolBar1(JToolBar toolBar) {
        super.initToolBar1(toolBar);
        toolBar.add(new WXBroadcastAction(this));
    }
}
