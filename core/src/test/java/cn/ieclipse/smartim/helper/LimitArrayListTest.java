package cn.ieclipse.smartim.helper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LimitArrayListTest {
    
    private int limit = 3;
    LimitArrayList<Integer> list;
    
    @Before
    public void setUp() throws Exception {
        list = new LimitArrayList<>(this.limit);
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testAddE() {
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        assertEquals(this.limit, list.size());
        assertEquals(4, list.get(this.limit - 1).intValue());
    }
    
    @Test
    public void testAddIntE() {
        try {
            list.add(0, 3);
        } catch (Exception e) {
            assertEquals(e.getClass(), UnsupportedOperationException.class);
        }
    }
    
    @Test
    public void testAddAllCollectionOfQextendsE() {
        List<Integer> c = Arrays.asList(0, 1);
        list.add(1);
        list.addAll(c);
        assertEquals(list, Arrays.asList(1, 0, 1));
        
        c = Arrays.asList(11, 12);
        list.addAll(c);
        assertEquals(list, Arrays.asList(1, 11, 12));
        
        c = Arrays.asList(21, 22, 23);
        list.addAll(c);
        assertEquals(list, c);
        
        c = Arrays.asList(31, 32, 33, 34);
        list.addAll(c);
        assertEquals(list, Arrays.asList(32, 33, 34));
    }
    
    @Test
    public void testAddAllIntCollectionOfQextendsE() {
        try {
            list.addAll(0, null);
        } catch (Exception e) {
            assertEquals(e.getClass(), UnsupportedOperationException.class);
        }
    }
    
}
