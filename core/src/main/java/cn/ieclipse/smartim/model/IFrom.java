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
package cn.ieclipse.smartim.model;

/**
 * 消息来源，有群消息（来自哪个群，哪个群成员发的）和普通用户消息（哪位好友发的）
 * 
 * @author Jamling
 * @date 2017年9月1日
 *       
 */
public interface IFrom {
    /**
     * 来源于哪个联系人或群组
     * 
     * @return 联系人或群组
     */
    IContact getContact();
    
    /**
     * 来源于联系人中的哪个成员，如果联系人为好友，则返回{@link #getContact()}
     * 
     * @return 群成员或null
     */
    IContact getMember();
    
    /**
     * 发送给哪个联系人的
     * 
     * @return 目标联系人或null（自己）
     */
    IContact getTarget();
    
    int DIR_IN = 0;
    int DIR_OUT = 1;
    int DIR_UNKNOWN = -1;
}
