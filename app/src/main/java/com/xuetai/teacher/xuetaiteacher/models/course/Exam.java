package com.xuetai.teacher.xuetaiteacher.models.course;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jinghao Zhang
 * Email: zhang.jing.hao@outlook.com
 * Date: 2019/1/25
 * {
 *  "id": 10040,
 * 	"wordsrc": null,
 * 	"examcn": null,
 * 	"ordernum": 10040,
 *  "examcnparentid": null,
 * 	"iconSkin": null,
 * 	"ishidden": null,
 * 	"head": "有理数的加法",
 * 	"exams": [],
 * 	"checked": true,
 * 	"wordext": null
 * }
 */
public class Exam {

    int id = 0;

    String head = "";

    List<Exam> exams = new ArrayList<>();

    boolean checked = false;

    public Exam() {
    }

    public Exam(int id, String head, List<Exam> exams) {
        this.id = id;
        this.head = head;
        this.exams = exams;
        this.checked = false;
    }

    public Exam(int id, String head, List<Exam> exams, boolean checked) {
        this.id = id;
        this.head = head;
        this.exams = exams;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public String getHead() {
        return head;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("\nID: ").append(id).append("\n")
                .append("Head: ").append(head).append("\n")
                .append("Checked: ").append(checked);

        for (Exam exam : exams) {
            tmp.append(exam.toString());
        }
        return tmp.toString();
    }
}
