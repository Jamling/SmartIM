package com.scienjus.smartqq.model;

import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.smartim.model.impl.AbstractFrom;

public class DiscussFrom extends AbstractFrom {
    
    private DiscussUser gu;
    private DiscussInfo discuss;
    
    public void setDiscussUser(DiscussUser gu) {
        this.gu = gu;
    }
    
    public DiscussUser getDiscussUser() {
        return gu;
    }
    
    public String getName() {
        return getDiscussUser().getName();
    }
    
    public DiscussInfo getDiscuss() {
        return discuss;
    }
    
    public void setDiscuss(DiscussInfo discuss) {
        this.discuss = discuss;
    }
    
    @Override
    public IContact getContact() {
        return discuss;
    }
}
