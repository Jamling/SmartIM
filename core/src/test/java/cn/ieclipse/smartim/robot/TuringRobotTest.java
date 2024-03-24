package cn.ieclipse.smartim.robot;

import cn.ieclipse.smartim.model.mock.MockContact;
import junit.framework.TestCase;
import org.junit.Test;

public class TuringRobotTest extends TestCase {

    TuringRobot robot;
    MockContact contact;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        robot = new TuringRobot("", "");
        contact = new MockContact("Jamling", "outernetsoft");
    }

    @Test
    public void testGetRobotAnswer() {
        String answer = null;
        try {
            answer = robot.getRobotAnswer("今天天气？", contact, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(answer);
        assertNotNull(answer);
    }
}