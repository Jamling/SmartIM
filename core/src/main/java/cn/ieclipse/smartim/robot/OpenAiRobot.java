package cn.ieclipse.smartim.robot;

import cn.ieclipse.smartim.model.IContact;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OpenAiRobot extends AbsRobot {
    private static final String BASE_URL = "https://api.openai.com/";
    private String sk;
    private CompletionRequest baseRequest;

    public OpenAiRobot(String key, String extra) {
        super(0, 0);
        this.sk = key;
        baseRequest = getGson().fromJson(extra, CompletionRequest.class);
    }

    @Override
    public void recycle() {
        super.recycle();
        baseRequest = null;
    }

    private String getRequestBody(String text, IContact contact, String groupId) {
        CompletionRequest req;
        try {
            req = (CompletionRequest) baseRequest.clone();
        } catch (Exception e) {
            return null;
        }
        if (req.echo == null) {
            req.echo = Boolean.TRUE;
        }
        if (req.top_p == null) {
            req.top_p = Double.valueOf(1);
        }
        if (req.best_of == null) {
            req.best_of = 1;
        }
        if (req.temperature == null) {
            req.temperature = Double.valueOf(1);
        }
        req.stream = false;
        req.prompt = text;
        req.user = contact.getUin();
        int max = req.max_tokens != null ? req.max_tokens : 2000;
        req.max_tokens = max - getPromptTokens(req.prompt);
        return getGson().toJson(req);
    }

    private int getPromptTokens(String prompt) {
        int res = prompt.length();
        for(int i=0;i < prompt.length();i++) {
            if (prompt.charAt(i) > 256) {
                res++;
            }
        }
        return res;
    }

    @Override
    public String getRobotAnswer(String question, IContact contact, String groupId) throws Exception {
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json;charset=utf-8"), getRequestBody(question, contact, groupId));
        Request request = new Request.Builder().url(BASE_URL + "v1/completions")
                .header("Authorization", "Bearer " + sk)
                .post(body)
                .build();
        Response response = getClient().newCall(request).execute();
        String result = response.body().string();
        JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
        if (obj.has("error")) {
            throw new IllegalArgumentException(obj.get("error").getAsJsonObject().get("message").getAsString());
        }
        System.out.println(result);
        if(obj.has("choices")) {
            String text = obj.get("choices").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
            System.out.println(text);
            return text;
        }
        return null;
    }


    /**
     * OkHttp Interceptor that adds an authorization token header
     */
    private static class AuthenticationInterceptor implements Interceptor {
        private String token;

        AuthenticationInterceptor(String token) {
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(request);
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class CompletionRequest implements Cloneable {

        /**
         * The name of the model to use.
         * Required if specifying a fine tuned model or if using the new v1/completions endpoint.
         */
        String model;

        /**
         * An optional prompt to complete from
         */
        String prompt;

        /**
         * The maximum number of tokens to generate.
         * Requests can use up to 4097 tokens shared between prompt and completion.
         * (One token is roughly 4 characters for normal English text)
         */
        Integer max_tokens;

        /**
         * What sampling temperature to use. Higher values means the model will take more risks.
         * Try 0.9 for more creative applications, and 0 (argmax sampling) for ones with a well-defined answer.
         * <p>
         * We generally recommend using this or {@link com.theokanning.openai.completion.CompletionRequest#topP} but not both.
         */
        Double temperature;

        /**
         * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
         * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are
         * considered.
         * <p>
         * We generally recommend using this or {@link com.theokanning.openai.completion.CompletionRequest#temperature} but not both.
         */
        Double top_p;

        /**
         * How many completions to generate for each prompt.
         * <p>
         * Because this parameter generates many completions, it can quickly consume your token quota.
         * Use carefully and ensure that you have reasonable settings for {@link com.theokanning.openai.completion.CompletionRequest#maxTokens} and {@link com.theokanning.openai.completion.CompletionRequest#stop}.
         */
        Integer n;

        /**
         * Whether to stream back partial progress.
         * If set, tokens will be sent as data-only server-sent events as they become available,
         * with the stream terminated by a data: DONE message.
         */
        Boolean stream;

        /**
         * Include the log probabilities on the logprobs most likely tokens, as well the chosen tokens.
         * For example, if logprobs is 10, the API will return a list of the 10 most likely tokens.
         * The API will always return the logprob of the sampled token,
         * so there may be up to logprobs+1 elements in the response.
         */
        Integer logprobs;

        /**
         * Echo back the prompt in addition to the completion
         */
        Boolean echo;

        /**
         * Up to 4 sequences where the API will stop generating further tokens.
         * The returned text will not contain the stop sequence.
         */
        List<String> stop;

        /**
         * Number between 0 and 1 (default 0) that penalizes new tokens based on whether they appear in the text so far.
         * Increases the model's likelihood to talk about new topics.
         */
        Double presence_penalty;

        /**
         * Number between 0 and 1 (default 0) that penalizes new tokens based on their existing frequency in the text so far.
         * Decreases the model's likelihood to repeat the same line verbatim.
         */
        Double frequency_penalty;

        /**
         * Generates best_of completions server-side and returns the "best"
         * (the one with the lowest log probability per token).
         * Results cannot be streamed.
         * <p>
         * When used with {@link com.theokanning.openai.completion.CompletionRequest#n}, best_of controls the number of candidate completions and n specifies how many to return,
         * best_of must be greater than n.
         */
        Integer best_of;

        /**
         * Modify the likelihood of specified tokens appearing in the completion.
         * <p>
         * Maps tokens (specified by their token ID in the GPT tokenizer) to an associated bias value from -100 to 100.
         * <p>
         * https://beta.openai.com/docs/api-reference/completions/create#completions/create-logit_bias
         */
        Map<String, Integer> logit_bias;

        /**
         * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
         */
        String user;

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
