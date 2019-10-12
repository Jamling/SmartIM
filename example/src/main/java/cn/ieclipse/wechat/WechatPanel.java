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
        loadWelcome("wechat");
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
        IMChatConsole console = new WXChatConsole(contact, this);
        console.setName(WXUtils.getPureName(contact.getName()));
        return console;
    }

    @Override
    protected void initToolBar1(JToolBar toolBar) {
        super.initToolBar1(toolBar);
        toolBar.add(new WXBroadcastAction(this));
    }
}
