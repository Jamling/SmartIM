package com.scienjus.smartqq.model;

/**
 * 讨论组消息.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 15/12/19.
 */
public class DiscussMessage extends QQMessage {
    
    private long discussId;
    
    public long getDiscussId() {
        return discussId;
    }
    
    public void setDiscussId(long discussId) {
        this.discussId = discussId;
    }
}
