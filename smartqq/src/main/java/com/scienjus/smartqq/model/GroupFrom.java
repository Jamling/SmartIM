package com.scienjus.smartqq.model;

import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.impl.AbstractFrom;

public class GroupFrom extends AbstractFrom {
    
    private GroupUser gu;
    private GroupInfo group;
    
    public void setGroupUser(GroupUser gu) {
        this.gu = gu;
    }
    
    public GroupUser getGroupUser() {
        return gu;
    }
    
    public String getName() {
        return getGroupUser().getName();
    }
    
    public GroupInfo getGroup() {
        return group;
    }
    
    public void setGroup(GroupInfo group) {
        this.group = group;
    }
    
    @Override
    public IContact getContact() {
        return getGroup();
    }
}
