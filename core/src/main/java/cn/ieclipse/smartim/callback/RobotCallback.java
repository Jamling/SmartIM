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
package cn.ieclipse.smartim.callback;

import cn.ieclipse.smartim.robot.IRobot;
import cn.ieclipse.util.EncryptUtils;

/**
 * 机器人回调接口
 * 
 * @author Jamling
 * @date 2023年2月12日
 * 
 */
public interface RobotCallback extends ReceiveCallback {
    String SEP = " ";
    boolean isEnable();

    String getRobotName();

    boolean isReplyAnyGroupMember();

    boolean isReplyFriend();

    String emptyReply();

    String getGroupWelcome();

    IRobot getRobot();

    /**
     * 对userId或groupId进行加密
     * 
     * @param id
     *            userId
     * @return 加密后的md5字串
     */
    default String encodeUid(String id) {
        return EncryptUtils.encryptMd5(id);
    }
}
