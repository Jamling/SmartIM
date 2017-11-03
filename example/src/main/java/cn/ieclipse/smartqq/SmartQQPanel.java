package cn.ieclipse.smartqq;

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
    
    //
    // private Map<String, QQChatConsole> consoles = new HashMap<>();
    //
    // public QQChatConsole findConsole(String name, boolean add) {
    // return consoles.get(name);
    // }
}
