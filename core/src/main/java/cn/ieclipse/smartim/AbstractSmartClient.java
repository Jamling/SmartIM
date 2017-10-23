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

import java.util.ArrayList;
import java.util.List;

import cn.ieclipse.smartim.callback.ModificationCallback;
import cn.ieclipse.smartim.callback.LoginCallback;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.callback.SendCallback;
import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.IMessage;
import cn.ieclipse.smartim.model.impl.AbstractFrom;
import cn.ieclipse.smartim.model.impl.AbstractMessage;

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
    
    @Override
    public boolean isClose() {
        return isClose;
    }
    
    @Override
    public boolean isLogin() {
        return isLogin;
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
    
    protected void notifySend(int type, String targetId, String message,
            Exception e) {
        if (sendCallback != null) {
            sendCallback.onSendResult(type, targetId, message, e == null, e);
        }
    }
}
