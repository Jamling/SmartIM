package cn.ieclipse.smartim.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.ieclipse.smartim.console.IMChatConsole;
import icons.SmartIcons;

public class SendFileAction extends IMChatAction {
    protected String dialogTitle;
    protected FileNameExtensionFilter filter_image =
        new FileNameExtensionFilter("图片文件", "jpg", "gif", "bmp", "jpeg", "png");
    protected FileFilter filter;

    public SendFileAction(IMChatConsole console) {
        super(console, SmartIcons.file);
        this.setToolTipText("发送文件");
        this.dialogTitle = "请选择要发送的文件";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!console.enableUpload()) {
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        if (filter != null) {
            chooser.setFileFilter(filter);
        }
        int code = chooser.showOpenDialog(new JLabel());
        if (code == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (f != null) {
                console.sendFile(f.getAbsolutePath());
            }
        }
        return;
    }

}
