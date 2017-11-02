/*
 * Copyright 2014-2017 ieclipse.cn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ieclipse.smartim.callback.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import cn.ieclipse.smartim.callback.LoginCallback;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月20日
 *       
 */
public class DefaultLoginCallback implements LoginCallback {
    QRCodeFrame qrCodeFrame;
    
    String tip;
    
    String title;
    
    public void setTitle(String title, String tip) {
        this.title = title;
        this.tip = tip;
    }
    
    private void initQrCodeFrame(final String path) {
        if (null != qrCodeFrame)
            qrCodeFrame.dispose();
        qrCodeFrame = new QRCodeFrame(path, title);
        qrCodeFrame.setTip(tip);
    }
    
    @Override
    public void onQrcode(final String path) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    initQrCodeFrame(path);
                } catch (Exception e) {
                    System.err.println("显示二维码失败" + e.toString());
                }
            }
        });
    }
    
    @Override
    public void onLogin(final boolean success, final Exception e) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (success) {
                    if (qrCodeFrame != null) {
                        qrCodeFrame.setTip("登录成功");
                        qrCodeFrame.setVisible(false);
                        qrCodeFrame.dispose();
                    }
                }
                else {
                    if (qrCodeFrame == null) {
                        qrCodeFrame = new QRCodeFrame(null, title);
                    }
                    qrCodeFrame.setError(e.toString());
                }
                onLoginFinish(success, e);
            }
        });
    }
    
    /**
     * Overwrite this method to do the work after login finished. This method
     * run on UI thread.
     * 
     * @param success
     *            login success or not
     * @param e
     *            exception when login failed
     */
    protected void onLoginFinish(final boolean success, final Exception e) {
    
    }
    
    public static class QRCodeFrame extends JFrame {
        private JPanel contentPane;
        private JTextArea tfError;
        private JLabel tfTip;
        private String title;
        private String error;
        private String tip;
        
        /**
         * Create the frame.
         */
        @SuppressWarnings("serial")
        public QRCodeFrame(final String filePath, final String title) {
            setBackground(Color.WHITE);
            this.setResizable(true);
            this.setTitle(title);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // this.setBounds(100, 100, 500, 400);
            this.contentPane = new JPanel();
            // contentPane.setBackground(new Color(102, 153, 255));
            this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            this.setContentPane(contentPane);
            contentPane.setLayout(new BorderLayout());
            
            final ImageIcon icon = new ImageIcon(filePath);
            JPanel qrcodePanel = new JPanel() {
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (icon.getImage() != null) {
                        icon.getImage().flush();
                    }
                    // 图片随窗体大小而变化
                    int x = 0, y = 0;
                    if (getWidth() > 0 && getWidth() > icon.getIconWidth()) {
                        x = (getWidth() - icon.getIconWidth()) / 2;
                    }
                    if (getHeight() > 0 && getHeight() > icon.getIconHeight()) {
                        y = (getHeight() - icon.getIconHeight()) / 2;
                    }
                    g.drawImage(icon.getImage(), x, y, icon.getIconWidth(),
                            icon.getIconHeight(), this);
                            
                }
            };
            qrcodePanel.setPreferredSize(
                    new Dimension(icon.getIconWidth(), icon.getIconHeight()));
            qrcodePanel.setOpaque(true);
            // qrcodePanel.setBackground(Color.WHITE);
            
            tfTip = new JLabel(tip == null ? title : tip);
            tfTip.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            tfTip.setHorizontalAlignment(SwingConstants.CENTER);
            tfTip.setOpaque(true);
            tfTip.setBackground(new Color(102, 153, 255));
            
            tfError = new JTextArea();
            tfError.setEditable(false);
            tfError.setBorder(new EmptyBorder(0, 0, 0, 0));
            tfError.setCaretColor(Color.RED);
            tfError.setForeground(Color.RED);
            tfError.setLineWrap(true);
            
            contentPane.add(tfTip, BorderLayout.NORTH);
            contentPane.add(qrcodePanel, BorderLayout.CENTER);
            contentPane.add(tfError, BorderLayout.SOUTH);
            
            this.setMinimumSize(new Dimension(300, 300));
            this.setMaximumSize(new Dimension(600, 600));
            pack();
            
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }
        
        public void setError(String string) {
            if (tfError != null) {
                tfError.setText(string);
            }
        }
        
        @Override
        public void setTitle(String title) {
            super.setTitle(title);
        }
        
        public void setTip(String tip) {
            this.tip = tip;
            if (tfTip != null) {
                tfTip.setText(tip);
            }
        }
        
        public static void main(String[] args) {
            new QRCodeFrame("qrcode.jpg", "微信登录");
        }
    }
}
