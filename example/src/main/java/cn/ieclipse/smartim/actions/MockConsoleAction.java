package cn.ieclipse.smartim.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import cn.ieclipse.smartim.common.IMUtils;
import cn.ieclipse.smartim.console.MockChatConsole;
import cn.ieclipse.smartim.model.impl.AbstractContact;
import cn.ieclipse.smartim.views.IMPanel;
import icons.SmartIcons;

/**
 * Created by Jamling on 2017/7/12.
 */
public class MockConsoleAction extends JButton implements ActionListener{
    IMPanel panel;

    public MockConsoleAction(IMPanel panel) {
        super(SmartIcons.test);
        this.panel = panel;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        AbstractContact contact = new AbstractContact() {
            @Override
            public String getName() {
                return "Test";
            }

            @Override
            public String getUin() {
                return "test";
            }
        };

        MockChatConsole console = new MockChatConsole(contact, panel);
        console.setName(contact.getName());
        panel.addConsole(console);
        panel.randBling();
        String msg = IMUtils.formatHtmlMsg(System.currentTimeMillis(), "明月",
                "我的未来不是梦http://www.baidu.com咕咕");
        console.write(msg);
    }
}
