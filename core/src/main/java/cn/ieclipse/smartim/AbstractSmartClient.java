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
package cn.ieclipse.smartim;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.FileUtil;
import cn.ieclipse.smartim.callback.LoginCallback;
import cn.ieclipse.smartim.callback.ModificationCallback;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.callback.SendCallback;
import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.impl.AbstractFrom;
import cn.ieclipse.smartim.model.impl.AbstractMessage;
import cn.ieclipse.util.FileUtils;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2017年10月14日
 *       
 */
public abstract class AbstractSmartClient implements SmartClient {
    
    protected ReceiveCallback receiveCallback;
    protected List<ReceiveCallback> receiveCallbacks = new ArrayList<>();
    protected List<MessageInterceptor> interceptors = new ArrayList<>();
    protected SendCallback sendCallback;
    protected ModificationCallback modificationCallback;
    
    protected LoginCallback loginCallback;
    protected boolean isLogin = false;
    protected boolean pollStarted = false;
    protected boolean isClose = false;
    protected String name = "";
    protected File workDir;
    
    @Override
    public boolean isClose() {
        return isClose;
    }
    
    @Override
    public boolean isLogin() {
        return isLogin;
    }
    
    @Override
    public void setWorkDir(File path) {
        if (path == null) {
            throw new IllegalArgumentException("Work directory is null");
        }
        if (!path.exists()) {
            path.mkdirs();
        }
        this.workDir = path;
        System.setProperty("log.home", path.getAbsolutePath());
        ILoggerFactory fac = LoggerFactory.getILoggerFactory();
        if (fac != null && fac instanceof LoggerContext) {
            LoggerContext lc = (LoggerContext) fac;
            lc.getStatusManager().clear();
            lc.reset();
            lc.putProperty("log.home", path.getAbsolutePath());
            ContextInitializer ci = new ContextInitializer(lc);
            try {
                ci.autoConfig();
            } catch (JoranException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void setSendCallback(SendCallback callback) {
        this.sendCallback = callback;
    }
    
    @Override
    public void setReceiveCallback(ReceiveCallback callback) {
        this.receiveCallback = callback;
    }
    
    @Override
    public void setLoginCallback(LoginCallback callback) {
        this.loginCallback = callback;
    }
    
    @Override
    public void addReceiveCallback(ReceiveCallback callback) {
        if (callback != receiveCallback
                && !receiveCallbacks.contains(callback)) {
            receiveCallbacks.add(callback);
        }
    }
    
    @Override
    public void addMessageInterceptor(MessageInterceptor interceptor) {
        if (!interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
        }
    }
    
    @Override
    public void setModificationCallbacdk(ModificationCallback callback) {
        this.modificationCallback = callback;
    }
    
    public void removeMessageInterceptor(MessageInterceptor interceptor) {
        interceptors.remove(interceptor);
    }
    
    protected void notifyReceive(AbstractMessage msg, AbstractFrom from) {
        if (receiveCallback != null) {
            receiveCallback.onReceiveMessage(msg, from);
        }
        if (receiveCallbacks != null) {
            for (ReceiveCallback callback : receiveCallbacks) {
                if (callback != null) {
                    callback.onReceiveMessage(msg, from);
                }
            }
        }
    }
    
    protected void notifySend(int type, String targetId, CharSequence message,
            Exception e) {
        if (sendCallback != null) {
            sendCallback.onSendResult(type, targetId, message, e == null, e);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public File getWorkDir(String name) {
        if (name == null || name.trim().length() == 0) {
            return workDir;
        }
        return new File(workDir, name);
    }
}
