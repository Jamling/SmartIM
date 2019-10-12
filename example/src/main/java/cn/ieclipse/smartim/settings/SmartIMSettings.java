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
package cn.ieclipse.smartim.settings;

import java.io.File;
import java.io.FileNotFoundException;

import cn.ieclipse.smartim.common.LOG;
import cn.ieclipse.util.FileUtils;

/**
 * 类/接口描述
 * 
 * @author Jamling
 * @date 2018年1月18日
 * 
 */
public class SmartIMSettings {
    private static final String CFG = "config.cfg";
    private State myState = new State();

    public State getState() {
        return myState;
    }

    public void loadState(State state) {
        this.myState = state;
    }

    public void loadProp() {
        try {
            this.myState = (State)FileUtils.readObject(new File("./"), CFG);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                LOG.warn("找不到配置文件 " + e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
        if (this.myState == null) {
            this.myState = new State();
        }
        if (this.myState.KEY_SEND == null) {
            this.myState.KEY_SEND = "Enter";
        }
    }

    public void saveProp() {
        try {
            FileUtils.writeObject(new File("."), CFG, myState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SmartIMSettings getInstance() {
        if (instance == null) {
            instance = null;
            if (instance == null) {
                instance = new SmartIMSettings();
            }
        }
        return instance;
    }

    private static SmartIMSettings instance;

    public static class State implements java.io.Serializable {
        private static final long serialVersionUID = -5719423461653118971L;
        public String KEY_SEND = "Enter";
        public boolean SHOW_SEND = false;
        public boolean NOTIFY_MSG = true;
        public boolean NOTIFY_FRIEND_MSG = true;
        public boolean NOTIFY_GROUP_MSG = false;
        public boolean NOTIFY_UNREAD = true;

        public boolean NOTIFY_UNKNOWN = false;
        public boolean HIDE_MY_INPUT = true;
        public boolean LOG_HISTORY = true;
        public String WORK_PATH = "";
        public int THEME = -1;

        public boolean ROBOT_ENABLE = false;
        public String ROBOT_NAME = "";
        public int ROBOT_TYPE = 0;
        public String ROBOT_KEY = "";
        public String ROBOT_GROUP_WELCOME = "欢迎{user} {memo}";
        public boolean ROBOT_GROUP_ANY = false;
        public boolean ROBOT_FRIEND_ANY = false;
        public String ROBOT_REPLY_EMPTY = "";

        public boolean QN_ENABLE = false;
        public String QN_BUCKET = "";
        public int QN_ZONE = 0;
        public String QN_DOMAIN = "";
        public String QN_AK = "";
        public String QN_SK = "";
        public boolean QN_TS = false;
    }
}
