package cn.ieclipse.smartim.robot;

import cn.ieclipse.smartim.model.IContact;
import com.google.gson.Gson;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;

public class OpenAiRobotTest extends TestCase {

    String appKey = "";


    @Test
    public void test() throws IOException {
        OpenAiRobot.CompletionRequest req = new OpenAiRobot.CompletionRequest();
        req.echo = false;
        req.model = "code-davinci-002";
        //req.model = "text-ada-001";
        OpenAiRobot robot = new OpenAiRobot(appKey, new Gson().toJson(req));
        String q = "var name = 'foo';";
        try {
            String r = robot.getRobotAnswer(q, new IContact() {
                @Override
                public String getName() {
                    return "test";
                }

                @Override
                public String getUin() {
                    return "test";
                }
            }, null);
            System.out.println("ans:" + r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        CompletionRequest request = new CompletionRequest();
        request.setModel("code-davinci-002");
        //request.setEcho(true);
        request.setPrompt("var name = 'ljm';");
        //request.setMaxTokens(2000);
        OpenAiService service = new OpenAiService(appKey, Duration.ZERO);
        CompletionResult result = service.createCompletion(request);
        System.out.println(result.getChoices());

    }
}