package com.lmdd.service;

import com.alibaba.fastjson.JSON;
import com.lmdd.pojo.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;


import javax.servlet.http.HttpServletRequest;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/webSocket/{username}")
@Component
public class WebSocketService {
    /**
     *  维护一个用户和 websocketSession的关系表
     *  静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */

    private static AtomicInteger onlineNum = new AtomicInteger();

    /**
     *     concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
     */
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                System.out.println("发送数据：" + message);
                session.getBasicRemote().sendText(message);
            }
        }
    }
    //给指定用户发送信息
    public void sendInfo(String userName, String message){
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 群发消息
    public void broadcast(String message){
        for (Session session: sessionPools.values()) {
            try {
                sendMessage(session, message);
            } catch(Exception e){
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 建立连接之后的业务逻辑
     * @param session
     * @param userName  用户名
     * @throws Exception
     */
    @OnOpen
    public void afterConnectionEstablished(Session session,@PathParam("username") String userName) throws Exception {
        sessionPools.put(userName, session);
        addOnlineCount();
        System.out.println("创建连接id: " + session.getId() + "建立连接");
        System.out.println("当前人数为" + onlineNum);
//        String username = (String) session.getAttributes().get("username");
//        String to = (String) session.getAttributes().get("to");
//
//        sessionUsers.put(username,session);
//
//        System.out.println("username:"+username);
//        System.out.println("to:"+to);
    }

    @OnMessage
    public void handleMessage(String message) throws IOException {
//        处理消息
        Message msg = JSON.parseObject(message, Message.class);
        System.out.println(msg);
        msg.setDate(new Date());
        // 如果携带消息中to值为-1 表明当前为群发消息
        if (msg.getTo().equals("-1")) {
            broadcast(JSON.toJSONString(msg,true));
        } else {
            // 给指定个体发送消息
            sendInfo(msg.getTo(), JSON.toJSONString(msg,true));
        }

    }
// 连接关闭后
    @OnClose
    public void afterConnectionClosed(Session session,@PathParam(value = "username") String userName) throws Exception {
        sessionPools.remove(userName);
        subOnlineCount();
        System.out.println(userName + "断开webSocket连接！当前人数为" + onlineNum);

    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }


    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    public static AtomicInteger getOnlineNumber() {
        return onlineNum;
    }

    public static ConcurrentHashMap<String, Session> getSessionPools() {
        return sessionPools;
    }

}