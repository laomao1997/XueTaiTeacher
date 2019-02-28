package com.xuetai.teacher.xuetaiteacher.models;

import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class TeacherInfo {
    private long examineStatus;
    private String phone;
    private String headPhoto;
    private String location;
    private String subject;
    private String idcardPhoto;
    private String education;
    private List<String> exp;
    private List<String> cert;
    private String school;
    private String idcardno;
    private String level;
    private String nick;
    private String realName;
    private String grade;
    private String gender;
    private String educationPhoto;
    private String schooltime;
    private String note;

    public long getExamineStatus() {
        return examineStatus;
    }

    public void setExamineStatus(long value) {
        this.examineStatus = value;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String value) {
        this.phone = value;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String value) {
        this.headPhoto = value;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String value) {
        this.location = value;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String value) {
        this.subject = value;
    }

    public String getIdcardPhoto() {
        return idcardPhoto;
    }

    public void setIdcardPhoto(String value) {
        this.idcardPhoto = value;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String value) {
        this.education = value;
    }

    public List getExp() {
        return exp;
    }

    public void setExp(List value) {
        this.exp = value;
    }

    public List getCERT() {
        return cert;
    }

    public void setCERT(List value) {
        this.cert = value;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String value) {
        this.school = value;
    }

    public String getIdcardno() {
        return idcardno;
    }

    public void setIdcardno(String value) {
        this.idcardno = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String value) {
        this.level = value;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String value) {
        this.nick = value;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String value) {
        this.realName = value;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String value) {
        this.grade = value;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    public String getEducationPhoto() {
        return educationPhoto;
    }

    public void setEducationPhoto(String value) {
        this.educationPhoto = value;
    }

    public String getSchooltime() {
        return schooltime;
    }

    public void setSchooltime(String value) {
        this.schooltime = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String value) {
        this.note = value;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("examine_status", examineStatus);
            jsonObject.put("phone", phone);
            jsonObject.put("head_photo", headPhoto);
            jsonObject.put("location", location);
            jsonObject.put("subject", subject);
            jsonObject.put("idcard_photo", idcardPhoto);
            jsonObject.put("education", education);
            jsonObject.put("school", school);
            jsonObject.put("idcardno", idcardno);
            jsonObject.put("level", level);
            jsonObject.put("nick", nick);
            jsonObject.put("real_name", realName);
            jsonObject.put("gender", gender);
            jsonObject.put("education_photo", educationPhoto);
            jsonObject.put("schooltime", schooltime);
            jsonObject.put("note", note);
            jsonObject.put("grade", grade);

//            JSONArray expJsonArray = new JSONArray();
//            JSONArray certJsonArray = new JSONArray();
//            JSONArray gradeJsonArray = new JSONArray();
//            for (String ex : exp) expJsonArray.put(ex);
//            for (String ce : cert) certJsonArray.put(ce);
//            for (int gr : grade) gradeJsonArray.put(gr);
//            jsonObject.put("exp", expJsonArray);
//            jsonObject.put("cert", certJsonArray);
//            jsonObject.put("grade", gradeJsonArray);
        } catch (Exception e) {
            KLog.e(e);
        }
        return jsonObject;
    }

    public void fromJsonObject(JSONObject jsonObject) {
        try {
            examineStatus = jsonObject.getLong("examine_status");
            phone = jsonObject.getString("phone");
            headPhoto = jsonObject.getString("head_photo");
            location = jsonObject.getString("location");
            subject = jsonObject.getString("subject");
            idcardPhoto = jsonObject.getString("idcard_photo");
            education = jsonObject.getString("education");
            school = jsonObject.getString("school");
            idcardno = jsonObject.getString("idcardno");
            level = jsonObject.getString("level");
            nick = jsonObject.getString("nick");
            realName = jsonObject.getString("real_name");
            gender = jsonObject.getString("gender");
            educationPhoto = jsonObject.getString("education_photo");
            schooltime = jsonObject.getString("schooltime");
            note = jsonObject.getString("note");
            grade = jsonObject.getString("grade");
//            for (int k = 0; k < gradeJson.length(); k++) {
//                if (isNumeric(gradeJson.charAt(k)))
//                    grade.add(Integer.parseInt(gradeJson.charAt(k) + ""));
//            }
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    public static boolean isNumeric(char c) {
        if (!Character.isDigit(c)) {
            return false;
        }
        return true;
    }
}
