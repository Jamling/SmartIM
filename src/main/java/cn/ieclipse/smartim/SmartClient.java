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

import cn.ieclipse.smartim.callback.LoginCallback;
import cn.ieclipse.smartim.callback.ModificationCallback;
import cn.ieclipse.smartim.callback.ReceiveCallback;
import cn.ieclipse.smartim.callback.SendCallback;
import cn.ieclipse.smartim.handler.MessageInterceptor;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.IMessage;

/**
 * IM抽象客户端
 * 
 * @author Jamling
 * @date 2017年10月13日
 *       
 */
public interface SmartClient {
    public static boolean DEBUG = true;
    
    public abstract void setWorkDir(File path);
    
    public abstract boolean isLogin();
    
    public abstract boolean isClose();
    
    public abstract void login();
    
    public abstract void init() throws Exception;
    
    public abstract void start();
    
    public abstract void close();
    
    public abstract IContact getAccount();
    
    public abstract void setLoginCallback(LoginCallback callback);
    
    public abstract void setSendCallback(SendCallback callback);
    
    public abstract void setReceiveCallback(ReceiveCallback callback);
    
    public abstract void addReceiveCallback(ReceiveCallback callback);
    
    public abstract int sendMessage(IMessage msg, IContact target)
            throws Exception;
            
    public abstract void addMessageInterceptor(MessageInterceptor interceptor);
    
    public abstract void setModificationCallbacdk(
            ModificationCallback callback);
}
