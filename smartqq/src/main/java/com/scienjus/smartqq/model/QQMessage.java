package com.scienjus.smartqq.model;

import java.util.ArrayList;
import java.util.List;

import cn.ieclipse.smartim.model.impl.AbstractMessage;

public class QQMessage extends AbstractMessage {
    private long time;
    
    private String content;
    
    private long userId;
    
    private Font font;
    
    private List<String> ats;
    
    private String result;
    
    public long getTime() {
        return time * 1000;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getContent(boolean trimAt) {
        if (getAts() == null) {
            return content;
        }
        for (String at : getAts()) {
            content = content.replaceAll(at, "");
        }
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
    
    public void addAt(String at) {
        if (ats == null) {
            ats = new ArrayList<>();
        }
        ats.add(at);
    }
    
    public boolean hasAt(String at) {
        if (ats != null) {
            return ats.contains("@" + at);
        }
        return false;
    }
    
    public List<String> getAts() {
        return ats;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    @Override
    public String toString() {
        return String.format("%s{from=%s, time=%s, content=%s, ats=%s}",
                getClass().getSimpleName(), getUserId(), time, content,
                (ats == null ? "" : ats));
    }
}
