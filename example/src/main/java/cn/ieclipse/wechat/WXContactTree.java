package cn.ieclipse.wechat;

import javax.swing.JTree;

import io.github.biezhi.wechat.model.Contact;

public class WXContactTree extends JTree {
    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
        boolean hasFocus) {
        if (value != null && value instanceof Contact) {
            return WXUtils.getPureName(((Contact)value).getName());
        }
        return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
    }
}
