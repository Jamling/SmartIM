package cn.ieclipse.smartim.robot;

import com.google.gson.Gson;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public abstract class AbsRobot implements IRobot {
    private OkHttpClient client;
    private Gson gson;

    public AbsRobot(int connectTimeout, int readTimeout) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout >= 0 ? connectTimeout : CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(readTimeout >= 0 ? readTimeout : READ_TIMEOUT, TimeUnit.SECONDS);
        builder.connectionPool(new ConnectionPool(5, 10, TimeUnit.SECONDS));
        initClientBuilder(builder);
        this.client = builder.build();
        this.gson = new Gson();
    }

    protected void initClientBuilder(OkHttpClient.Builder builder) {
    }

    protected final OkHttpClient getClient() {
        return client;
    }

    protected final Gson getGson() {
        return gson;
    }

    public void recycle() {
        client = null;
        gson = null;
    }
}
