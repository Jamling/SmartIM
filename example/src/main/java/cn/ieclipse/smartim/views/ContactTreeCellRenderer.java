package cn.ieclipse.smartim.views;

import cn.ieclipse.smartim.model.IContact;
import com.scienjus.smartqq.model.Discuss;
import com.scienjus.smartqq.model.Group;
import com.scienjus.smartqq.model.Recent;
import icons.SmartIcons;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Created by Jamling on 2017/7/13.
 */
public class ContactTreeCellRenderer extends DefaultTreeCellRenderer {

    public ContactTreeCellRenderer() {}

    public Icon getDisplayIcon(Object obj) {
        return SmartIcons.friend;
    }

    public String getDisplayName(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof IContact) {
            return ((IContact)obj).getName();
        }
        return obj.toString();
    }
}
