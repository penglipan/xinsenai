package cn.liuzhengquan.controller;

import cn.liuzhengquan.entity.Friends;
import cn.liuzhengquan.entity.Messages;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.concurrent.TimeUnit;
import java.net.Proxy;
import java.net.InetSocketAddress;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{nickname}")
public class WebSocketController {
    private Session session;
    private static final String GPT_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String GPT_API_KEY = "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";  // Replace with your actual secret key

    public static List<Friends> friendsList = new ArrayList<>();
    public static ConcurrentHashMap<String, WebSocketController> webSocketSession = new ConcurrentHashMap<>();

    public static synchronized void sendP2PMessage(String nickname, String message) {
        log.info("&#8203;``【oaicite:0】``&#8203;点对点发送消息, nickname={} , message={}", nickname, message);
        try {
            webSocketSession.get(nickname).session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("点对点发送异常:", e);
        }
    }

    @OnOpen
    public void onOpen(@PathParam(value = "nickname") String nickname, Session session) {
        this.session = session;
        webSocketSession.put(nickname, this);
        friendsList.add(Friends.builder().nickname(nickname).build());
        updateFriendInformationList();

        log.info("&#8203;``【oaicite:3】``&#8203;有新的连接[{}], 连接总数:{}", nickname, webSocketSession.size());

        if (!nickname.endsWith("1")) {
            String aiNickname = nickname + "1";
            createAISession(aiNickname);
        }
    }

    private void createAISession(String aiNickname) {
        webSocketSession.put(aiNickname, this);
    }

    @OnMessage
    public void onMessage(@PathParam(value = "nickname") String nickname, String message) {
        log.info("&#8203;``【oaicite:2】``&#8203; 收到客户端[{}] 发送消息:{} 连接总数:{}", nickname, message, webSocketSession.size());

        if (StringUtils.hasLength(message)) {
            try {
                Messages messages = JSON.parseObject(message, Messages.class);
                sendP2PMessage(messages.getReceiveNickname(), message);

                if (!nickname.endsWith("1")) {
                    String aiResponse = getGptResponse(message);
                    String aiNickname = nickname + "1";
                    sendP2PMessage(aiNickname, aiResponse);
                }
            } catch (Exception e) {
                log.error("WebSocket消息异常:", e);
            }
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "nickname") String nickname) {
        friendsList.remove(friendsList.stream().filter((friends -> friends.getNickname().equals(nickname))).findAny().orElse(null));
        webSocketSession.remove(nickname);
        updateFriendInformationList();
        log.info("&#8203;``【oaicite:1】``&#8203;客户端[{}]连接断开, 剩余连接总数:{}", nickname, webSocketSession.size());
    }

    private synchronized void updateFriendInformationList() {
        webSocketSession.forEach((key, val) -> {
            List<Friends> friends = new ArrayList<>();
            friendsList.forEach((friend) -> {
                if (!friend.getNickname().equals(key)) {
                    friends.add(friend);
                }
            });
            sendP2PMessage(key, JSON.toJSONString(Messages.builder().type("updateFriendsList").receiveNickname(key).messages(friends).build()));
        });
    }


    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_SECONDS = 5;

    private String getGptResponse(String prompt) {

//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 30 seconds connection timeout
                .readTimeout(30, TimeUnit.SECONDS)     // 30 seconds read timeout
                .build();

        MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
        com.alibaba.fastjson.JSONObject jsonBody = new com.alibaba.fastjson.JSONObject();

        // 更新此部分以匹配新的请求格式
        com.alibaba.fastjson.JSONArray messagesArray = new com.alibaba.fastjson.JSONArray();
        messagesArray.add(new com.alibaba.fastjson.JSONObject().fluentPut("role", "user").fluentPut("content", prompt));
        jsonBody.put("messages", messagesArray);
        jsonBody.put("model", "gpt-3.5-turbo");
        jsonBody.put("temperature", 0.7);

        RequestBody body = RequestBody.create(JSON_TYPE, jsonBody.toJSONString());

        Request request = new Request.Builder()
                .url(GPT_ENDPOINT)
                .header("Authorization", "Bearer " + GPT_API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                com.alibaba.fastjson.JSONObject responseObject = com.alibaba.fastjson.JSONObject.parseObject(responseBody);
                return responseObject.getJSONArray("choices").getJSONObject(0).getString("text").trim();
            } catch (IOException e) {
                log.error("Attempt #" + (i + 1) + " failed: " + e.getMessage(), e);
                if (i == MAX_RETRIES - 1) { // If it's the last retry
                    return "Sorry, I couldn't generate a response.";
                }
                try {
                    TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS); // Wait before next retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return "Sorry, an unexpected error occurred.";
    }
}