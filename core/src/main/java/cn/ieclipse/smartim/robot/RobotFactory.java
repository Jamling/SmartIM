package cn.ieclipse.smartim.robot;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class RobotFactory {
    public static final int ROBOT_TURING = 0;
    public static final int ROBOT_OPENAI = 1;
    private Map<Integer, IRobot> robotMap;
    private IRobot robot;
    private IRobot.RobotSettingsChangedListener listener;

    private RobotFactory() {
        this.robotMap = new HashMap<>(2);
    }

    public static RobotFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public IRobot getRobot() {
        return robot;
    }

    public boolean changeSettings(int robot, String key, String extra) {
        IRobot newRobot;
        try {
            if (robot == ROBOT_OPENAI) {
                newRobot = new OpenAiRobot(key, extra);
            } else if (robot == ROBOT_TURING) {
                newRobot = new TuringRobot(key, extra);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        this.robot = robotMap.get(robot);
        if (this.robot != null) {
            this.robot.recycle();
        }
        this.robot = newRobot;
        robotMap.put(robot, this.robot);
        return true;
    }

    private static class SingletonHolder {
        private static RobotFactory INSTANCE = new RobotFactory();
    }
}
