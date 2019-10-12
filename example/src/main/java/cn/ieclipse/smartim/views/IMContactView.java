package cn.ieclipse.smartim.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeCellRenderer;

import cn.ieclipse.smartim.SmartClient;
import cn.ieclipse.smartim.callback.ModificationCallback;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.callback.SendCallback;

/**
 * Created by Jamling on 2017/7/11.
 */
public abstract class IMContactView extends JPanel {

    protected IMPanel imPanel;
    protected JTabbedPane tabHost;

    protected ReceiveCallback receiveCallback;
    protected SendCallback sendCallback;
    protected ReceiveCallback robotCallback;
    protected ModificationCallback modificationCallback;

    public IMContactView(IMPanel imPanel) {
        this.imPanel = imPanel;
        setLayout(new BorderLayout(0, 0));

        tabHost = new JTabbedPane(JTabbedPane.TOP);
        add(tabHost, BorderLayout.CENTER);

        // JScrollPane scrollPane = new JScrollPane();
        // tabHost.addTab("New tab", null, scrollPane, null);
    }

    public abstract JPanel getPanel();

    public IMPanel getImPanel() {
        return imPanel;
    }

    protected void initTrees(JTree... trees) {
        for (JTree tree : trees) {
            if (tree != null) {
                initTree(tree);
            }
        }
    }

    protected void initTree(JTree tree) {
        tree.setCellRenderer(new ContactTreeCellRenderer());
        tree.setShowsRootHandles(false);
        tree.setRootVisible(false);
        tree.addMouseListener(new IMContactDoubleClicker(getImPanel()));
    }

    public void updateTrees(JTree... trees) {
        for (JTree tree : trees) {
            if (tree != null) {
                tree.updateUI();
            }
        }
    }

    public void initContacts() {
        new Thread() {
            public void run() {
                doLoadContacts();
            }
        }.start();
    }

    protected abstract SmartClient getClient();

    protected abstract void doLoadContacts();

    protected abstract void onLoadContacts(boolean success);

    protected TreeCellRenderer getContactRenderer() {
        return new ContactTreeCellRenderer();
    }

    protected void notifyLoadContacts(final boolean success) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                onLoadContacts(success);
            }
        });
    }

    public void notifyUpdateContacts(final int index, boolean force) {
        boolean notify = true;
        if (notify || force) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    doUpdateContacts(index);
                }
            });
        }
    }

    protected void doUpdateContacts(final int index) {

    }
}
