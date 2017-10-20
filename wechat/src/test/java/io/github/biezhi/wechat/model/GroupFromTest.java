package io.github.biezhi.wechat.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GroupFromTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test() {
        List<Contact> groups = new ArrayList<Contact>();
        Contact g1 = new Contact();
        g1.UserName = "@@1";
        g1.NickName = "g1";
        groups.add(g1);
        
        Contact g2 = new Contact();
        g2.UserName = "@@1";
        g2.NickName = "g2";
        
        for (int i = 0; i < groups.size(); i++) {
            Contact g = groups.get(i);
            if (g.UserName.equals(g2.UserName)) {
                g = g2;
                System.out.println(g);
                groups.set(i, g2);
                break;
            }
        }
        System.out.println(groups);
        assertEquals("g2", groups.get(0).NickName);
    }
    
}
