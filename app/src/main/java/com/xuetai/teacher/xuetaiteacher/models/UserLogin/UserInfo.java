package com.xuetai.teacher.xuetaiteacher.models.UserLogin;


import com.xuetai.teacher.xuetaiteacher.models.Result;

import java.util.List;

/**
 * Created by XinSheng on 16/9/21.
 */
public class UserInfo extends Result {

    /**
     * "examine_status": 3,
     * "phone": "13333333333",
     * "head_photo": "",
     * "location": "",
     * "subject": "2",
     * "idcard_photo": "",
     * "education": "2",
     * "exp": [],
     * "cert": [],
     * "school": "测试学校",
     * "idcardno": "230103199999999999",
     * "level": "1",
     * "nick": "测试",
     * "real_name": "测试",
     * "grade": "[1,2,3]",
     * "gender": "0",
     * "education_photo": "http://39.107.70.183:8008/upload/teacher_info/a6c3e49d0830e4513fe8725bbdb5739b.png",
     * "schooltime": "2011-2015",
     * "note": "测试老师"
     */

    private String phone;
    private String real_name;
    private String nick;
    private String gender;
    private String note;
    private String location;
    private String level;
    private String grade;
    private String subject;
    private String idcardno;
    private String education;
    private String school;
    private String schooltime;
    private String head_photo;
    private String idcard_photo;
    private String education_photo;
    private String examine_status;
    private String examine_info;

    private List<?> exp;
    private List<?> cert;

    public List<?> getCert() {
        return cert;
    }

    public void setCert(List<?> cert) {
        this.cert = cert;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIdcardno() {
        return idcardno;
    }

    public void setIdcardno(String idcardno) {
        this.idcardno = idcardno;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchooltime() {
        return schooltime;
    }

    public void setSchooltime(String schooltime) {
        this.schooltime = schooltime;
    }

    public String getHead_photo() {
        return head_photo;
    }

    public void setHead_photo(String head_photo) {
        this.head_photo = head_photo;
    }

    public String getIdcard_photo() {
        return idcard_photo;
    }

    public void setIdcard_photo(String idcard_photo) {
        this.idcard_photo = idcard_photo;
    }

    public String getEducation_photo() {
        return education_photo;
    }

    public void setEducation_photo(String education_photo) {
        this.education_photo = education_photo;
    }

    public String getExamine_status() {
        return examine_status;
    }

    public void setExamine_status(String examine_status) {
        this.examine_status = examine_status;
    }

    public String getExamine_info() {
        return examine_info;
    }

    public void setExamine_info(String examine_info) {
        this.examine_info = examine_info;
    }

    public List<?> getExp() {
        return exp;
    }

    public void setExp(List<?> exp) {
        this.exp = exp;
    }
}
