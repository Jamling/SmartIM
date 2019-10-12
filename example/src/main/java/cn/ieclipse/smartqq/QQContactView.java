package cn.ieclipse.smartqq;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.scienjus.smartqq.client.SmartQQClient;

import cn.ieclipse.smartim.IMSendCallback;
import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.smartim.views.ContactTreeMode;
import cn.ieclipse.smartim.views.IMContactView;

/**
 * Created by Jamling on 2017/7/11.
 */
public class QQContactView extends IMContactView {
    private JPanel panel;

    private JTree recentTree;
    private JTree friendTree;
    private JTree groupTree;
    private JTree discussTree;

    private QQContactTreeNode root1, root2, root3, root4;

    private ContactTreeMode recentModel;
    private ContactTreeMode friendModel;
    private ContactTreeMode groupModel;
    private ContactTreeMode discussModel;

    public QQContactView(SmartQQPanel imPanel) {
        super(imPanel);

        recentTree = new JTree();
        friendTree = new JTree();
        groupTree = new JTree();
        discussTree = new JTree();

        JScrollPane scrollPane1 = new JScrollPane(recentTree);
        tabHost.addTab("最近", null, scrollPane1, null);

        JScrollPane scrollPane2 = new JScrollPane(friendTree);
        tabHost.addTab("好友", null, scrollPane2, null);

        JScrollPane scrollPane3 = new JScrollPane(groupTree);
        tabHost.addTab("群组", null, scrollPane3, null);

        JScrollPane scrollPane4 = new JScrollPane(discussTree);
        tabHost.addTab("讨论组", null, scrollPane4, null);

        receiveCallback = new QQReceiveCallback(imPanel);
        sendCallback = new IMSendCallback(imPanel);
        robotCallback = new QQRobotCallback(imPanel);
        modificationCallback = new QQModificationCallback(imPanel);

        root1 = new QQContactTreeNode(false, "recent", imPanel);
        root2 = new QQContactTreeNode(false, "friend", imPanel);
        root3 = new QQContactTreeNode(false, "group", imPanel);
        root4 = new QQContactTreeNode(false, "discuss", imPanel);

        initTrees(recentTree, friendTree, groupTree, discussTree);
        init();
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public SmartQQPanel getImPanel() {
        return (SmartQQPanel)super.getImPanel();
    }

    public void init() {
        recentModel = new ContactTreeMode(root1);
        friendModel = new ContactTreeMode(root2);
        groupModel = new ContactTreeMode(root3);
        discussModel = new ContactTreeMode(root4);

        recentTree.setModel(recentModel);
        friendTree.setModel(friendModel);
        groupTree.setModel(groupModel);
        discussTree.setModel(discussModel);
    }

    @Override
    protected TreeCellRenderer getContactRenderer() {
        return new QQContactTreeCellRenderer();
    }

    @Override
    protected SmartQQClient getClient() {
        return getImPanel().getClient();
    }

    @Override
    protected void doLoadContacts() {
        SmartQQClient client = getClient();
        if (client.isLogin()) {
            try {
                client.init();
                notifyLoadContacts(true);

                client.setReceiveCallback(receiveCallback);
                client.addReceiveCallback(robotCallback);
                client.setSendCallback(sendCallback);
                client.setModificationCallbacdk(modificationCallback);
                client.start();
            } catch (Exception e) {
                LOG.error("SmartQQ初始化失败", e);
            }
        } else {
            notifyLoadContacts(false);
        }
    }

    @Override
    protected void onLoadContacts(boolean success) {
        root1.update();
        root2.update();
        root3.update();
        root4.update();
        if (success) {
            init();
        }
        updateTrees(recentTree, friendTree, groupTree, discussTree);
    }

    @Override
    protected void doUpdateContacts(int index) {
        if (index == 0) {
            root1.update();
            updateTrees(recentTree);
        }
    }
}
