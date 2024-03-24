package cn.ieclipse.smartim.robot;

import cn.ieclipse.smartim.model.IContact;
import cn.ieclipse.util.StringUtils;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.core.builder.CompletionBuilder;
import com.baidubce.qianfan.model.completion.CompletionResponse;

public class BaiduRobot extends AbsRobot {
    Qianfan qianfan;
    Config config;
    public BaiduRobot(String apiKey, String extra) {
        super(0, 0);
        config = getGson().fromJson(extra, Config.class);
        qianfan = new Qianfan(Auth.TYPE_OAUTH,config.ak, config.sk);
    }

    @Override
    public String getRobotAnswer(String question, IContact contact, String groupId) throws Exception {
        CompletionBuilder completionBuilder = qianfan.completion();
        if (StringUtils.isEmpty(config.model)) {
            completionBuilder.model(config.model);
        }

        CompletionResponse response = completionBuilder.prompt(question).execute();
        return response.getResult();
    }

    public static class Config {
        public String ak;
        public String sk;
        public String model = "Yi-34B-Chat";
    }
}
