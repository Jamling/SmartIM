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
package cn.ieclipse.smartim.robot;

import cn.ieclipse.smartim.model.IContact;

/**
 * 机器人
 * 
 * @author Jamling
 * @date 2017年10月20日
 *       
 */
public interface IRobot {
    int CONNECT_TIMEOUT = 5;
    int READ_TIMEOUT = 5;
    
    String getRobotAnswer(String question, IContact contact, String groupId)
            throws Exception;

    void recycle();

    interface RobotSettingsChangedListener {

    }
}
