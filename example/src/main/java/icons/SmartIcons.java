/*
 * Copyright 2014-2017 ieclipse.cn.
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
package icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年11月2日
 * 
 */
public class SmartIcons {
    public static abstract class IconLoader {
        public static Icon getIcon(String path) {
            return new ImageIcon(IconLoader.class.getResource(path));
        }
    }

    public static Icon qq = IconLoader.getIcon("/icons/QQ.png");
    public static Icon wechat = IconLoader.getIcon("/icons/wechat.png");

    public static Icon signin = IconLoader.getIcon("/icons/sign-in.png");
    public static Icon signout = IconLoader.getIcon("/icons/sign-out.png");
    public static Icon test = IconLoader.getIcon("/icons/gitlab.png");
    public static Icon close = IconLoader.getIcon("/icons/close.png");
    public static Icon show = IconLoader.getIcon("/icons/eye.png");
    public static Icon hide = IconLoader.getIcon("/icons/eye-slash.png");
    public static Icon broadcast = IconLoader.getIcon("/icons/broadcast.png");
    public static Icon settings = IconLoader.getIcon("/icons/settings.png");

    public static Icon group = IconLoader.getIcon("/icons/user-circle.png");
    public static Icon friend = IconLoader.getIcon("/icons/user.png");
    public static Icon discuss = IconLoader.getIcon("/icons/user-o.png");
    public static Icon subscriber = IconLoader.getIcon("/icons/subscriber.png");
    public static Icon app3rd = IconLoader.getIcon("/icons/app.png");

    public static Icon file = IconLoader.getIcon("/icons/File.png");
    public static Icon image = IconLoader.getIcon("/icons/image.png");
    public static Icon face = IconLoader.getIcon("/icons/face.png");
    public static Icon lock = IconLoader.getIcon("/icons/lock_co.png");
    public static Icon clear = IconLoader.getIcon("/icons/clear_co.png");

    public static void main(String[] args) {
        System.out.println(group);
    }

}
