package com.xuetai.teacher.xuetaiteacher.models;

public class TreePoint {
    private int id;        // 7241,          //账号id
    private String name; // "用户原因",    //原因名称
    private int pid;   // 0,           //父id     0表示父节点
    private boolean isLeaf;     //0,            //是否是叶子节点   1为叶子节点
    private int level; // 1       //级别
    private boolean isExpand = false;  //是否展开了
    private boolean isSelected = false; //是否选中了


    public TreePoint(int id, String name, int pid, boolean isLeaf, int level) {
        this.id = id;
        this.name = name;
        this.pid = pid;
        this.isLeaf = isLeaf;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setNAME(String NAME) {
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "ID:" + id + " NAME:" + name + " PID:" + pid + " LEVEL:" + level + " isLeaf:" + isLeaf
                + " isExpand:" + isExpand + " isSelect:" + isSelected;
    }
}
