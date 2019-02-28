package com.xuetai.teacher.xuetaiteacher.models.course;

/**
 * Author: Jinghao Zhang
 * Email: zhang.jing.hao@outlook.com
 * Date: 2019/1/25
 */
public class TextBook {

    private int id;

    private String name;

    public TextBook(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
