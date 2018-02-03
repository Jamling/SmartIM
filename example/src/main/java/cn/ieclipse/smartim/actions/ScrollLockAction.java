package cn.ieclipse.smartim.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import cn.ieclipse.smartim.console.IMChatConsole;
import icons.SmartIcons;

public class ScrollLockAction extends JToggleButton implements ActionListener {
    IMChatConsole console;
    
    public ScrollLockAction(IMChatConsole console) {
        super(SmartIcons.lock);
        this.setToolTipText("禁止滚动");
        this.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        console.setScrollLock(isSelected());
    }
}
