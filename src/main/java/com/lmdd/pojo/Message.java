package com.lmdd.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * WebSocket 传递的消息类
 * @author LM_MAX
 * @date 2022/2/25
 */
public class Message {
    /**
     * 发送者name
     */
    public String from;
    /**
     * 接收者name
     */
    public String to;
    /**
     * 发送的文本
     */
    public String text;
    /**
     * 发送时间 JsonFormat json格式化输出时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    public Date date;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
