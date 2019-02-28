package com.xuetai.teacher.xuetaiteacher.models;

import java.io.Serializable;

/**
 * Author: Jinghao Zhang
 * Email: zhang.jing.hao@outlook.com
 * Date: 2019/2/25
 *
 * "id": "20220"                     评论ID
 * "time": "2018-09-11"              评论时间
 * "studentName": "梦儿"             学生姓名
 * "score": "3"                      3好评 2中评 1差评
 * "comment": "好评！"                评论内容
 * "reply": "呵呵"                   回复内容
 */
public class CommentBean implements Serializable {

    String id;
    String time;
    String studentName;
    String score;
    String comment;
    String reply;

    public CommentBean(String id, String time, String studentName, String score, String comment, String reply) {
        this.id = id;
        this.time = time;
        this.studentName = studentName;
        this.score = score;
        this.comment = comment;
        this.reply = reply;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean hasReply() {
        return !reply.isEmpty();
    }
}
