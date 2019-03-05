package com.xuetai.teacher.xuetaiteacher.models;

public class Chat {

    private String timeStamp; // 时间戳
    private String message;   // 消息内容
    private String from;      // 1:来自老师; 2:来自学生; 3:来自系统
    private String type;      // text:文字; img:图片

    public Chat(String timeStamp, String message, String from, String type) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.from = from;
        this.type = type;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
