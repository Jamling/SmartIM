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
package cn.ieclipse.smartim.callback;

/**
 * 登录回调
 * 
 * @author Jamling
 * @date 2017年6月16日
 *       
 */
public interface LoginCallback {
    /**
     * 二维码登录获取的二维码图片保存路径
     * 
     * @param path
     *            二维码保存路径
     */
    void onQrcode(String path);
    
    /**
     * 登录回调
     * 
     * @param success
     *            是否登录成功
     * @param e
     *            登录异常
     */
    void onLogin(boolean success, Exception e);
}
