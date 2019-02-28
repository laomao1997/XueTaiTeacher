package com.xuetai.teacher.xuetaiteacher.models;

import java.io.Serializable;

public class CourseBean implements Serializable {

    private String id;
    private String beginTime;
    private String time;
    private String text;
    private String date;
    private String status;

    public CourseBean() {
    }

    public CourseBean(String id, String beginTime, String time, String text, String date, String status) {
        this.id = id;
        this.beginTime = beginTime;
        this.time = time;
        this.text = text;
        this.date = date;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSoldStatus() {
        return this.status.equals("2");
    }
}
