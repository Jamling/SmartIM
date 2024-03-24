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

import cn.ieclipse.common.BareBonesBrowserLaunch;
import cn.ieclipse.util.XPathUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.text.html.StyleSheet;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 类/接口描述
 *
 * @author Jamling
 * @date 2019年1月16日
 */
public class RestUtils {
    public final static String CSS_URL = "http://dl.ieclipse.cn/r/smartim.css";
    public final static String UPDATE_URL = "http://dl.ieclipse.cn/jws/SmartIM-App.jnlp";
    public final static String ABOUT_URL = "http://dl.ieclipse.cn/jws/about.html";
    public static final String VERSION = "3.0.0";

    public static String getWelcome(String im) {
        if (im.equals("qq")) {
            return "因腾讯业务调整，SmartQQ于2019年1月1日起停止服务。当前暂无替代的协议，暂无法提供脑出血，敬请谅解！";
        }
        return "欢迎使用SmartIM Java Swing版";
    }

    public static void checkUpdate() {
        new Thread(() -> {
            try {
                Request.Builder builder = (new Request.Builder()).url(RestUtils.UPDATE_URL).get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response response = call.execute();
                if (response.code() == 200) {
                    Document doc = XPathUtils.parse(response.body().byteStream());
                    Element jnlp = XPathUtils.findElement(doc, "/jnlp");
                    final String latest = XPathUtils.findElement(jnlp, "version").getTextContent();
                    String codebase = jnlp.getAttribute("codebase");
                    Element jar = XPathUtils.findElement(jnlp, "resources/jar");
                    final String link = String.format("%s/%s", codebase, jar.getAttribute("href"));
                    SwingUtilities.invokeLater(() -> {
                        String msg =
                                String.format("当前版本：%s\n最新版本：%s，是否下载？\n下载地址：%s", VERSION, latest, link);
                        int ret = JOptionPane.showConfirmDialog(null, msg, "更新", JOptionPane.OK_CANCEL_OPTION);
                        if (ret == JOptionPane.OK_OPTION) {
                            BareBonesBrowserLaunch.openURL(link);
                        }
                    });
                }
            } catch (Exception ex) {
                LOG.error("检查SmartIM最新版本", ex);
            }
        }).start();
    }

    public static void loadStyleAsync(final StyleSheet styleSheet) {
        new Thread(() -> loadStyleSync(styleSheet)).start();
    }

    public static void loadStyleSync(final StyleSheet styleSheet) {
        try {
            styleSheet.importStyleSheet(new URL("http://dl.ieclipse.cn/r/smartim.css"));
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
    }
}
