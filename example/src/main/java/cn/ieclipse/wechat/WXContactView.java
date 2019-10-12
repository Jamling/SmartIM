package cn.ieclipse.wechat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import cn.ieclipse.smartim.IMSendCallback;
import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.smartim.views.ContactTreeMode;
import cn.ieclipse.smartim.views.IMContactView;
import io.github.biezhi.wechat.api.WechatClient;

/**
 * Created by Jamling on 2017/7/11.
 */
public class WXContactView extends IMContactView {
    private JPanel panel;

    private JTree recentTree;
    private JTree friendTree;
    private JTree groupTree;
    private JTree discussTree;

    private WXContactTreeNode root1, root2, root3, root4;

    private ContactTreeMode recentModel;
    private ContactTreeMode friendModel;
    private ContactTreeMode groupModel;
    private ContactTreeMode discussModel;

    public WXContactView(WechatPanel imPanel) {
        super(imPanel);

        recentTree = new WXContactTree();
        friendTree = new WXContactTree();
        groupTree = new WXContactTree();
        discussTree = new WXContactTree();

        JScrollPane scrollPane1 = new JScrollPane(recentTree);
        tabHost.addTab("最近", null, scrollPane1, null);

        JScrollPane scrollPane2 = new JScrollPane(friendTree);
        tabHost.addTab("好友", null, scrollPane2, null);

        // JScrollPane scrollPane3 = new JScrollPane(groupTree);
        // tabHost.addTab("群组", null, scrollPane3, null);

        JScrollPane scrollPane4 = new JScrollPane(discussTree);
        tabHost.addTab("公众号", null, scrollPane4, null);

        receiveCallback = new WXReceiveCallback(imPanel);
        sendCallback = new IMSendCallback(imPanel);
        robotCallback = new WXRobotCallback(imPanel);
        modificationCallback = new WXModificationCallback(imPanel);

        root1 = new WXContactTreeNode(false, "recent", imPanel);
        root2 = new WXContactTreeNode(false, "friend", imPanel);
        // root3 = new WXContactTreeNode(false, "group", imPanel);
        root4 = new WXContactTreeNode(false, "public", imPanel);

        initTrees(recentTree, friendTree, groupTree, discussTree);

        init();
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public WechatPanel getImPanel() {
        return (WechatPanel)super.getImPanel();
    }

    public void init() {
        recentModel = new ContactTreeMode(root1);
        friendModel = new ContactTreeMode(root2);
        // groupModel = new ContactTreeMode(root3);
        discussModel = new ContactTreeMode(root4);

        recentTree.setModel(recentModel);
        friendTree.setModel(friendModel);
        // groupTree.setModel(groupModel);
        discussTree.setModel(discussModel);
    }

    @Override
    protected void initTree(JTree tree) {
        super.initTree(tree);
    }

    @Override
    protected TreeCellRenderer getContactRenderer() {
        return new WXContactTreeCellRenderer();
    }

    @Override
    protected WechatClient getClient() {
        return getImPanel().getClient();
    }

    @Override
    protected void doLoadContacts() {
        WechatClient client = getClient();
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
                LOG.error("微信初始化失败", e);
            }
        } else {
            notifyLoadContacts(false);
        }
    }

    @Override
    protected void onLoadContacts(boolean success) {
        root1.update();
        root2.update();
        // root3.update();
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
