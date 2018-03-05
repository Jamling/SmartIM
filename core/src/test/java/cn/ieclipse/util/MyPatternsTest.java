package cn.ieclipse.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MyPatternsTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void test() {
        String regex = Patterns.WEB_URL.pattern();
        System.out.println(regex);
        System.out.println("httP://".matches(Patterns.PROTOCOL));
    }
    
}
