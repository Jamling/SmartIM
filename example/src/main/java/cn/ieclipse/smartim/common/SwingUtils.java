/*
 * Copyright 2014-2018 ieclipse.cn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package cn.ieclipse.smartim.common;

import java.awt.event.KeyEvent;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import cn.ieclipse.smartim.settings.SmartIMSettings;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2018年4月12日
 * 
 */
public class SwingUtils {
    public static boolean setLookAndFeel(String name, boolean className) {
        LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                LookAndFeelInfo info = infos[i];
                boolean f =
                    (!className && info.getName().equals(name)) || (className && info.getClassName().equals(name));
                if (f) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                        SmartIMSettings.getInstance().getState().THEME = i;
                        return true;
                    } catch (Exception e) {

                    }
                }
                // System.out.println(info);
            }
        }
        return false;
    }

    public static boolean setLookAndFeel(int theme) {
        if (theme > 0) {
            LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
            if (infos != null && theme < infos.length) {
                try {
                    UIManager.setLookAndFeel(infos[theme].getClassName());
                    return true;
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    public static void initOSLookAndFeel() {
        // String os = System.getProperty("os.name").toLowerCase();
        // if (os.indexOf("windows") >= 0) {
        // setLookAndFeel("Windows", false);
        // }
        // else if (os.indexOf("linux") >= 0) {
        // setLookAndFeel("GTK+", false);
        // }
        // else if (os.indexOf("mac") >= 0) {
        // setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel", true);
        // }
        // else
        {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName(), true);
        }
    }

    public static void initLookAndFeel() {
        int theme = SmartIMSettings.getInstance().getState().THEME;
        if (!setLookAndFeel(theme)) {
            initOSLookAndFeel();
        }
    }

    public static String key2string(KeyEvent e) {
        String key = "";
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            key = "Enter";
        }
        if (e.isShiftDown()) {
            key = "Shit + " + key;
        }
        if (e.isControlDown()) {
            key = "Ctrl + " + key;
        }
        return key;
    }
}
