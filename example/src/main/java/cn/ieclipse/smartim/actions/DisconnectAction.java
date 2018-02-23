package cn.ieclipse.smartim.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import cn.ieclipse.smartim.views.IMPanel;
import icons.SmartIcons;

/**
 * Created by Jamling on 2017/7/12.
 */
public class DisconnectAction extends JButton implements ActionListener{
    IMPanel panel;

    public DisconnectAction(IMPanel panel) {
        super(SmartIcons.close);
        this.panel = panel;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        panel.close();
    }
}
