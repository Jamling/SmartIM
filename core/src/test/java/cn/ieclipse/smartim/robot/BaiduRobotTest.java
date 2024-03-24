package cn.ieclipse.smartim.robot;

import com.google.gson.Gson;
import junit.framework.TestCase;

public class BaiduRobotTest extends TestCase {
    BaiduRobot robot;
    public void setUp() throws Exception {
        super.setUp();
        BaiduRobot.Config config = new BaiduRobot.Config();
        config.ak = "";
        config.sk = "";
        robot = new BaiduRobot("", new Gson().toJson(config));

    }

    public void testGetRobotAnswer() {
        try {
            String ret = robot.getRobotAnswer("今天天气", null, null);
            System.out.println(ret);
            assertNotNull(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}