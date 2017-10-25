package com.scienjus.smartqq.model;

import cn.ieclipse.smartim.model.impl.AbstractContact;

/**
 * 讨论组成员.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/24.
 */
public class DiscussUser extends AbstractContact {
    
    public long uin;
    public String nick;
    public int clientType;
    public String status;
    
    @Override
    public String toString() {
        return "DiscussUser{" + "uin=" + uin + ", nick='" + nick + '\''
                + ", clientType='" + clientType + '\'' + ", status='" + status
                + '\'' + '}';
    }
    
    @Override
    public String getUin() {
        return String.valueOf(uin);
    }
    
    @Override
    public String getName() {
        return nick;
    }
}
