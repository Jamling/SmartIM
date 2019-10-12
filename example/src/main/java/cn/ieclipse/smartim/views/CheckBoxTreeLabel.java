package cn.ieclipse.smartim.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class CheckBoxTreeLabel extends JLabel {
    private boolean isSelected;
    private boolean hasFocus;

    public CheckBoxTreeLabel() {}

    @Override
    public void setBackground(Color color) {
        if (color instanceof ColorUIResource)
            color = null;
        super.setBackground(color);
    }

    @Override
    public void paint(Graphics g) {
        String str;
        if ((str = getText()) != null) {
            if (0 < str.length()) {
                if (isSelected)
                    g.setColor(UIManager.getColor("Tree.selectionBackground"));
                else
                    g.setColor(UIManager.getColor("Tree.textBackground"));
                Dimension d = getPreferredSize();
                int imageOffset = 0;
                Icon currentIcon = getIcon();
                if (currentIcon != null)
                    imageOffset = currentIcon.getIconWidth() + Math.max(0, getIconTextGap() - 1);
                g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height);
                if (hasFocus) {
                    g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
                    g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1);
                }
            }
        }
        super.paint(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension retDimension = super.getPreferredSize();
        if (retDimension != null)
            retDimension = new Dimension(retDimension.width + 3, retDimension.height);
        return retDimension;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }
}