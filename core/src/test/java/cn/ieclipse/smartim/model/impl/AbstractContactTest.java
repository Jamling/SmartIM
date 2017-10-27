package cn.ieclipse.smartim.model.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.ieclipse.smartim.model.mock.MockContact;
import cn.ieclipse.smartim.model.mock.MockMessage;

public class AbstractContactTest {
    
    MockContact contact;
    
    @Before
    public void setUp() throws Exception {
        contact = new MockContact("J", "1");
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testIsUnknown() {
        assertEquals(false, contact.isUnknown());
    }
    
    @Test
    public void testSetUnknown() {
        contact.setUnknown(true);
        assertEquals(true, contact.isUnknown());
    }
    
    @Test
    public void testIsNewbie() {
        assertEquals(false, contact.isNewbie());
    }
    
    @Test
    public void testSetNewbie() {
        contact.setNewbie(true);
        assertEquals(true, contact.isNewbie());
    }
    
    @Test
    public void testGetLastMessage() {
        assertEquals(null, contact.getLastMessage());
    }
    
    @Test
    public void testSetLastMessage() {
        MockMessage m = new MockMessage("m1");
        contact.setLastMessage(m);
        MockMessage m2 = new MockMessage("m2");
        contact.setLastMessage(m2);
        assertEquals(m2, contact.getLastMessage());
    }
    
    @Test
    public void testGetUnread() {
        assertEquals(0, contact.getUnread());
    }
    
    @Test
    public void testSetUnread() {
        contact.setUnread(1);
        assertEquals(1, contact.getUnread());
    }
    
    @Test
    public void testClearUnRead() {
        contact.setUnread(10);
        contact.clearUnRead();
        assertEquals(0, contact.getUnread());
    }
    
    @Test
    public void testIncreaceUnRead() {
        contact.increaceUnRead();
        assertEquals(1, contact.getUnread());
        contact.increaceUnRead();
        assertEquals(2, contact.getUnread());
    }
    
    @Test
    public void testCompareTo() {
        MockContact contact2 = new MockContact("K", "2");
        List<MockContact> list = new ArrayList<>();
        list.add(contact);
        list.add(contact2);
        
        Collections.sort(list);
        assertEquals(contact, list.get(0));
        
        //
        
        MockMessage m = new MockMessage("m1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MockMessage m2 = new MockMessage("m2");
        System.out.println("m1 time:" + m.getTime() + " m2 time: " + m2.getTime());
        
        contact2.setLastMessage(m2);
        Collections.sort(list);
        assertEquals(contact2, list.get(0));
        
        contact.setLastMessage(m);
        Collections.sort(list);
        assertEquals(contact2, list.get(0));
        
        contact2.setLastMessage(null);
        Collections.sort(list);
        assertEquals(contact, list.get(0));
        
    }
    
}
