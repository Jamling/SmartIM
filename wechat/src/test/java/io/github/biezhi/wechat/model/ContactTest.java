package io.github.biezhi.wechat.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class ContactTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    WechatMessage create(long time) {
        WechatMessage m = new WechatMessage();
        m.CreateTime = time;
        return m;
    }
    
    @Test
    public void testComprare() {
        Contact top = new Contact();
        top.UserName = "top";
        top.ContactFlag = Contact.CONTACTFLAG_TOPCONTACT | 3;
        
        Contact c1 = new Contact();
        c1.setLastMessage(create(1000));
        c1.UserName = "c1";
        
        Contact c2 = new Contact();
        c2.UserName = "c2";
        
        ArrayList<Contact> list = new ArrayList<>();
        list.add(c2);
        list.add(c1);
        list.add(top);
        Collections.sort(list);
        assertEquals(top, list.get(0));
        assertEquals(c1, list.get(1));
        assertEquals(c2, list.get(2));
        
        c2.setLastMessage(create(2000));
        Collections.sort(list);
        assertEquals(top, list.get(0));
        assertEquals(c2, list.get(1));
        assertEquals(c1, list.get(2));
        
        c2.setLastMessage(null);
        Collections.sort(list);
        assertEquals(top, list.get(0));
        assertEquals(c1, list.get(1));
        assertEquals(c2, list.get(2));
    }
    
}
