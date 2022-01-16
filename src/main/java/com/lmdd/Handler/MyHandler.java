package com.lmdd.Handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyHandler extends TextWebSocketHandler {

//

    // 维护一个用户和 websocketSession的关系表
    public static final Map<String, WebSocketSession> sessionUsers = new ConcurrentHashMap<String, WebSocketSession>();

    // 当连接建立以后
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("创建连接id: "+ session.getId()+"建立连接");

        String username = (String) session.getAttributes().get("username");
        String to = (String) session.getAttributes().get("to");


        sessionUsers.put(username,session);

        System.out.println("username:"+username);
        System.out.println("to:"+to);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        处理消息
        String clientMessage = message.getPayload();

        System.out.println(clientMessage);
        String username = (String) session.getAttributes().get("username");
        String to = (String) session.getAttributes().get("to");

        // 发送给to 用户
        WebSocketSession toSession = sessionUsers.get(to);
        toSession.sendMessage(new TextMessage(clientMessage));


//        if (clientMessage.startsWith("Hello") || clientMessage.startsWith("Hi")) {
//            session.sendMessage(new TextMessage("Hello! What can i do for you?"));
//        } else {
//            session.sendMessage(
//                    new TextMessage("This is a simple hello world example of using Spring WebSocket."));
//        }
    }
// 连接关闭后
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        System.out.println(session.getId()+ "————》 afterConnectionClosed()");

        // 关闭一个连接后应该干啥
        for (Map.Entry<String, WebSocketSession> entry : sessionUsers.entrySet()) {
            System.out.println(entry.getKey()+"----->"+entry.getValue().getId());
        }
        super.afterConnectionClosed(session, status);
    }
}