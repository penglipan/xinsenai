package cn.liuzhengquan.controller;

import cn.liuzhengquan.dto.ChatRequest;
import cn.liuzhengquan.dto.ChatResponse;
import cn.liuzhengquan.entity.Friends;
import cn.liuzhengquan.entity.Messages;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import javax.websocket.*;
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

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired
    @Qualifier("openaiRestTemplate")
    private RestTemplate restTemplate;

    public static List<Friends> friendsList = new ArrayList<>();
    public static ConcurrentHashMap<String, WebSocketController> webSocketSession = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(@PathParam(value = "nickname") String nickname, Session session) {
        this.session = session;
        webSocketSession.put(nickname, this);
        friendsList.add(Friends.builder().nickname(nickname).build());
        updateFriendInformationList();
        log.info("有新的连接[{}], 连接总数:{}", nickname, webSocketSession.size());

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
        log.info("收到客户端[{}] 发送消息:{} 连接总数:{}", nickname, message, webSocketSession.size());

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
        log.info("客户端[{}]连接断开, 剩余连接总数:{}", nickname, webSocketSession.size());
    }

    public static synchronized void sendP2PMessage(String nickname, String message) {
        log.info("点对点发送消息, nickname={} , message={}", nickname, message);
        try {
            webSocketSession.get(nickname).session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("点对点发送异常:", e);
        }
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

    private String getGptResponse(String userInput) {
        ChatRequest chatRequest = new ChatRequest(model, userInput);
        ChatResponse chatResponse;

        try {
            chatResponse = restTemplate.postForObject(apiUrl, chatRequest, ChatResponse.class);

            if (chatResponse != null && !chatResponse.getChoices().isEmpty()) {
                return chatResponse.getChoices().get(0).getMessage().getContent().trim();
            }
        } catch (Exception e) {
            log.error("Error processing message:", e);
        }
        return "Sorry, an unexpected error occurred.";
    }
}
