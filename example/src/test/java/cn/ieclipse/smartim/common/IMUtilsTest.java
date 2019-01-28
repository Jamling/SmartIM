package cn.ieclipse.smartim.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import cn.ieclipse.util.Patterns;

public class IMUtilsTest {
    String regex = "(https?|ftp|file)://(([\\w-~]+)\\.)+([\\w-~\\/])+(((?!\\.)(\\S))+(\\.\\w+(\\?(\\w+=\\S&?)*)?)?)?";
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testAutoLink() {
        String msg = "普通文本abc";
        String html = IMUtils.autoLink(msg);
        System.out.println(html);
        assertEquals(msg, html);
        msg = "中 www.baidu.com abc";
        html = IMUtils.autoLink(msg);
        System.out.println(html);
        assertEquals("中 <a href=\"http://www.baidu.com\">www.baidu.com</a> abc",
                html);
        html = IMUtils.autoLink(html);
        System.out.println(html);
        assertEquals("中 <a href=\"http://www.baidu.com\">www.baidu.com</a> abc",
                html);
        msg = "<a href=\"file:///C:/Users/Jamling/Documents/Tencent OD Files/media/image_3998395364719518041.jpg\" title=\"点击查看大图\"><img src=\"file:///C:/Users/Jamling/Documents/Tencent OD Files/media/image_3998395364719518041.jpg\" width=\"67\" height=\"144\" alt=\"图片\"/></a>";
        html = IMUtils.autoLink(msg);
        assertEquals(msg, html);
    }
    
    @Test
    public void testAutoLink2() {
        String msg = "qhttps://www.baidu.com/test/中%DE%DE%DE我的.html";
        List<String> tests = Arrays.asList(msg, "http://wab.com/",
                "一段http://abc.com中文", "http://t.cn/abc def", "https://t.cn/我的",
                "https://t.cn/我的 gogo", "http://t.cn/我的.abc",
                "https://t.cn/我的.txt 中", "http://t.cn/我的?n=我去",
                "ftp://t.cn/?n=我去", "https://t.cn/我的?n=我去&b=33",
                "http://abc.com/2/f1.0.2(9)-release.apk dd", "百度www.baidu.com",
                "图片https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_86d58ae1.png你造么");
        for (String str : tests) {
            Matcher m = Patterns.WEB_URL.matcher(str);
            if (m.find()) {
                String url = m.group();
                System.out.println(url);
                assertTrue("Matches", true);
                
                String chstr = "(.+?)(" + IMUtils.UCS_CHAR + "+$)";
                url = url.replaceAll(chstr, "$1");
                chstr = "^(" + IMUtils.UCS_CHAR + "+)" + "(.+?)";
                url = url.replaceAll(chstr, "$2");
                System.out.println("->" + url);
            }
            else {
                fail(str + " Not matches");
            }
        }
    }
    
    @Test
    public void testProtocol() {
        String s = "https://ddf";
        Pattern p = Pattern.compile(Patterns.PROTOCOL);
        assertEquals(true, p.matcher(s).find());
    }
}
