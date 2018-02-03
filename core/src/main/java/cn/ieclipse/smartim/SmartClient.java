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
    
    public String getName();
    
    public void setWorkDir(File path);
    
    public File getWorkDir(String name);
    
    public boolean isLogin();
    
    public boolean isClose();
    
    public void login();
    
    public void init() throws Exception;
    
    public void start();
    
    public void close();
    
    public IContact getAccount();
    
    public void setLoginCallback(LoginCallback callback);
    
    public void setSendCallback(SendCallback callback);
    
    public void setReceiveCallback(ReceiveCallback callback);
    
    public void addReceiveCallback(ReceiveCallback callback);
    
    public int sendMessage(IMessage msg, IContact target)
            throws Exception;
            
    public void addMessageInterceptor(MessageInterceptor interceptor);
    
    public void setModificationCallbacdk(
            ModificationCallback callback);
}
