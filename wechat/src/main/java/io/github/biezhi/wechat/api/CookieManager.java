package io.github.biezhi.wechat.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieManager implements CookieJar {
    private List<Cookie> cookies;
    
    public List<Cookie> getCookies() {
        return cookies;
    }
    
    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
    
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (this.cookies == null) {
            this.cookies = new ArrayList<>();
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                Cookie old = getFirstCookie(cookie.name());
                if (old != null) {
                    if (old.value() == null || old.value().equals("")) {
                        this.cookies.remove(old);
                    }
                }
                this.cookies.add(cookie);
            }
        }
    }
    
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> matched = new ArrayList<Cookie>();
        if (this.cookies != null) {
            for (Cookie c : this.cookies) {
                if (c.matches(url)) {
                    matched.add(c);
                }
            }
        }
        // Collections.sort(matched, sorter);
        return matched;
    }
    
    public Cookie getFirstCookie(String name) {
        if (this.cookies != null) {
            for (Cookie c : this.cookies) {
                if (c.name().equals(name)) {
                    return c;
                }
            }
        }
        return null;
    }
    
    public void remove(String name) {
        if (this.cookies != null) {
            List<Cookie> removes = new ArrayList<>();
            for (Cookie c : this.cookies) {
                if (c.name().equals(name)) {
                    removes.add(c);
                }
            }
            for (Cookie c : removes) {
                this.cookies.remove(c);
            }
        }
    }
    
    private CookieSort sorter = new CookieSort();
    
    private class CookieSort implements Comparator<Cookie> {
        
        @Override
        public int compare(Cookie o1, Cookie o2) {
            return o1.name().compareTo(o2.name());
        }
    }
}
