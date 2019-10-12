package cn.ieclipse.smartqq;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.Discuss;
import com.scienjus.smartqq.model.Friend;
import com.scienjus.smartqq.model.Group;

import cn.ieclipse.smartim.actions.BroadcastAction;
import cn.ieclipse.smartim.dialogs.BroadcastDialog;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.IMessage;
import cn.ieclipse.smartim.views.ContactTreeMode;
import cn.ieclipse.smartim.views.IMPanel;

public class QQBroadcastDialog extends BroadcastDialog {
    private JTree recentTree;
    private JTree friendTree;
    private JTree groupTree;
    private JTree discussTree;
    private QQContactTreeNode root1, root2, root3, root4;
    private ContactTreeMode recentModel;
    private ContactTreeMode friendModel;
    private ContactTreeMode groupModel;
    private ContactTreeMode discussModel;

    public QQBroadcastDialog(IMPanel panel) {
        super(panel);
    }

    @Override
    protected void initTab(JTabbedPane host) {
        // recentTree = new JTree();
        friendTree = new JTree();
        groupTree = new JTree();
        discussTree = new JTree();

        // JScrollPane scrollPane1 = new JScrollPane(recentTree);
        // tabHost.addTab("最近", null, scrollPane1, null);

        JScrollPane scrollPane2 = new JScrollPane(friendTree);
        tabHost.addTab("好友", null, scrollPane2, null);

        JScrollPane scrollPane3 = new JScrollPane(groupTree);
        tabHost.addTab("群组", null, scrollPane3, null);

        JScrollPane scrollPane4 = new JScrollPane(discussTree);
        tabHost.addTab("讨论组", null, scrollPane4, null);

        initTrees(recentTree, friendTree, groupTree, discussTree);
        // root1 = new QQContactTreeNode(false, "recent", imPanel);
        root2 = new QQContactTreeNode(false, "friend", imPanel);
        root3 = new QQContactTreeNode(false, "group", imPanel);
        root4 = new QQContactTreeNode(false, "discuss", imPanel);
        root2.update();
        root3.update();
        root4.update();

        // recentModel = new ContactTreeMode(root1);
        friendModel = new ContactTreeMode(root2);
        groupModel = new ContactTreeMode(root3);
        discussModel = new ContactTreeMode(root4);

        // recentTree.setModel(recentModel);
        friendTree.setModel(friendModel);
        groupTree.setModel(groupModel);
        discussTree.setModel(discussModel);

    }

    protected void sendInternal(String text, List<Object> targets) {
        SmartQQClient client = (SmartQQClient)imPanel.getClient();
        int ret = 0;
        if (targets != null) {
            for (Object obj : targets) {
                if (obj != null && obj instanceof IContact) {
                    IContact target = (IContact)obj;
                    try {
                        IMessage m = createMessage(text, target, client);
                        client.sendMessage(m, target);
                        ret++;
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    protected IMessage createMessage(String text, IContact target, SmartQQClient client) {
        IMessage m = null;
        if (!client.isClose()) {
            if (target instanceof Friend) {
                m = client.createMessage(text, target);
            } else if (target instanceof Group || target instanceof Discuss) {
                String msg = text.replace(BroadcastAction.groupMacro, target.getName());
                m = client.createMessage(msg, target);
            }
        }
        return m;
    }
}
