package com.scienjus.smartqq.model;

import com.google.gson.annotations.SerializedName;

import cn.ieclipse.smartim.model.impl.AbstractContact;

/**
 * 讨论组.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 2015/12/23.
 */
public class Discuss extends AbstractContact {
    
    @SerializedName("did")
    public long id;
    public String name;
    
    public String getName() {
        return name;
    }
    
    public long getId() {
        return id;
    }
    
    @Override
    public String getUin() {
        return String.valueOf(id);
    }
}
