package cn.ieclipse.smartqq;

import javax.swing.JToolBar;

import com.scienjus.smartqq.client.SmartQQClient;

import cn.ieclipse.smartim.IMClientFactory;
import cn.ieclipse.smartim.console.IMChatConsole;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.views.IMContactView;
import cn.ieclipse.smartim.views.IMPanel;

/**
 * Created by Jamling on 2017/7/11.
 */
public class SmartQQPanel extends IMPanel {

    public SmartQQPanel() {
        super();
        loadWelcome("qq");
    }

    @Override
    public SmartQQClient getClient() {
        return IMClientFactory.getInstance().getQQClient();
    }

    @Override
    public IMContactView createContactsUI() {
        return new QQContactView(this);
    }

    @Override
    public IMChatConsole createConsoleUI(IContact contact) {
        return new QQChatConsole(contact, this);
    }

    @Override
    protected void initToolBar1(JToolBar toolBar) {
        super.initToolBar1(toolBar);
        toolBar.add(new QQBroadcastAction(this));
    }
}
