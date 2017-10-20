package com.scienjus.smartqq.model;

import com.google.gson.annotations.SerializedName;

import cn.ieclipse.smartim.model.impl.AbstractContact;

/**
 * 群.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/18.
 */
public class Group extends AbstractContact {
    
    @SerializedName("gid")
    public long id;
    
    public String name;
    
    public long flag;
    
    public long code;
    
    public String getName() {
        return name;
    }
    
    @Override
    public String getUin() {
        return String.valueOf(id);
    }
}
