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
package cn.ieclipse.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年11月3日
 *       
 */
public class ScaleIcon implements Icon {
    
    private ImageIcon icon;
    
    public ScaleIcon(ImageIcon icon) {
        this.icon = icon;
    }
    
    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        float wid = c.getWidth();
        float hei = c.getHeight();
        int iconWid = icon.getIconWidth();
        int iconHei = icon.getIconHeight();
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.scale(wid / iconWid, hei / iconHei);
        icon.paintIcon(c, g2d, 0, 0);
    }
    
    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }
    
    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }
}
