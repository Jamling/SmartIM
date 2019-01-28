package cn.ieclipse.wechat;

import static org.junit.Assert.*;

import java.net.URLEncoder;

import org.junit.Before;
import org.junit.Test;

import cn.ieclipse.smartim.common.IMUtils;
import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.util.StringUtils;
import io.github.biezhi.wechat.model.Contact;

public class WXUtilsTest {
    
    @Before
    public void setUp() throws Exception {
        String src = "file:///C:/Users/Jamling/Desktop/磊.png";
    }
    
    @Test
    public void testGetContactChar() {
        Contact contact = new Contact();
        
        char c = WXUtils.getContactChar(contact);
        assertEquals(c, 'F');
    }
    
    @Test
    public void testDecodeEmoji() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testGetPureName() {
        String n = WXUtils
                .getPureName("一个emoji<span class=\"emoji emoji23434\"></span>");
        assertEquals("一个emoji", n);
        n = WXUtils.getPureName(
                "2个emoji<span class=\"emoji emoji23434\"></span> <span class=\"emoji emoji23434\"></span>");
        assertEquals("2个emoji", n);
        n = WXUtils.getPureName(
                "2个emoji<span class=\"emoji emoji23434\"></span>中<span class=\"emoji emoji23434\"></span>");
        assertEquals("2个emoji中", n);
        n = WXUtils.getPureName(
                "一个emoji<span class=\"emoji emoji23434\"></span>一个emoji");
        assertEquals("一个emoji一个emoji", n);
    }
}
