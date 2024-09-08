package io.github.biezhi.wechat.api;

import io.github.biezhi.wechat.model.Session;

import java.io.Serializable;
import java.util.Map;

public class LoginData implements Serializable {

    Map<String, Object> baseRequest;
    Session session;
    String cookie;

}
