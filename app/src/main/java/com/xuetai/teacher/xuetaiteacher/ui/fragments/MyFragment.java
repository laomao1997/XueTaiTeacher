package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.ui.activities.CourseSettingActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.BasicSettingActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.MyEvaluationActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.MyIncomeActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.MyPersonalInfoActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFragment extends Fragment {

    String iconUrl;
    String realName;
    String subject;
    String grades;
    String level;
    String phone;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_real_name)
    TextView tvRealName;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;

    @BindView(R.id.ly_personal_info)
    RelativeLayout mLyPersonalInfo;
    @BindView(R.id.ly_my_income)
    RelativeLayout mLyMyIncome;
    @BindView(R.id.ly_my_comment)
    RelativeLayout mLyMyComment;
    @BindView(R.id.ly_lesson_setup)
    RelativeLayout mLyLessonSetup;
    @BindView(R.id.ly_normal_setup)
    RelativeLayout mLyNormalSetup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my, null);
        ButterKnife.bind(this, v);

        loadInfo();

        return v;
    }

    private void loadInfo() {
        try {
            SharedPreferences userPreference = getActivity().getSharedPreferences("setting", 0);
            String userInfo = userPreference.getString("info", "no_info");
            JSONObject infoJson = new JSONObject(userInfo);
//            KLog.json(userInfo);
            iconUrl = infoJson.getString("head_photo");
            realName = infoJson.getString("real_name");
            subject = infoJson.getString("subject");
            grades = infoJson.getString("grade");
            level = infoJson.getString("level");
            phone = infoJson.getString("phone");
            loadAvatar(iconUrl);
            loadSubTitle(subject, grades, level);
            loadName(realName);
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    /**
     * 加载姓名
     *
     * @param name 姓名
     */
    private void loadName(String name) {
        tvRealName.setText(name);
    }

    /**
     * 加载教学科目和年级
     *
     * @param subject 科目 2数学 3英语 4物理 5化学 6生物 8地理 1语文 7历史
     * @param grades  年级 [1,2,3]
     * @param level   阶段 1初中 2高中
     */
    private void loadSubTitle(String subject, String grades, String level) {
        String textSubject = "";
        StringBuilder textGrades = new StringBuilder();

        ArrayList<Integer> gradeList = getGradesList(grades);
        switch (subject) {
            case "2":
                textSubject += "数学老师";
                break;
            case "3":
                textSubject += "英语老师";
                break;
            case "4":
                textSubject += "物理老师";
                break;
            case "5":
                textSubject += "化学老师";
                break;
            case "6":
                textSubject += "生物老师";
                break;
            case "8":
                textSubject += "地理老师";
                break;
            case "1":
                textSubject += "语文老师";
                break;
            case "7":
                textSubject += "历史老师";
                break;
        }

        if (level.equals("1")) {
            if (gradeList.size() == 3) {
                textGrades.append("初一、初二、初三");
            } else {
                for (int i : gradeList) {
                    switch (i) {
                        case 1:
                            textGrades.append("初一");
                            break;
                        case 2:
                            textGrades.append("初二");
                            break;
                        case 3:
                            textGrades.append("初三");
                            break;
                    }
                }
            }
        } else {
            if (gradeList.size() == 3) {
                textGrades.append("高一、高二、高三");
            } else {
                for (int i : gradeList) {
                    switch (i) {
                        case 4:
                            textGrades.append("高一");
                            break;
                        case 5:
                            textGrades.append("高二");
                            break;
                        case 6:
                            textGrades.append("高三");
                            break;
                    }
                }
            }
        }
        if (textGrades.length() == 4) {
            textGrades.insert(2, "、");
        }

        tvSubTitle.setText(textSubject + "  " + textGrades);
    }

    /**
     * 解析JSON传过来的年级字符串 成为一个list
     *
     * @param grade 年级字符串 - 服务器的锅 本来应该传一个JSON数组过来的
     * @return
     */
    private ArrayList<Integer> getGradesList(String grade) {
        ArrayList<Integer> gradeList = new ArrayList<>();

        switch (grade.length()) {
            case 3:
                gradeList.add(Integer.parseInt(grade.charAt(1) + ""));
                break;
            case 5:
                gradeList.add(Integer.parseInt(grade.charAt(1) + ""));
                gradeList.add(Integer.parseInt(grade.charAt(3) + ""));
                break;
            case 7:
                gradeList.add(Integer.parseInt(grade.charAt(1) + ""));
                gradeList.add(Integer.parseInt(grade.charAt(3) + ""));
                gradeList.add(Integer.parseInt(grade.charAt(5) + ""));
                break;
        }
        return gradeList;
    }

    /**
     * 加载头像
     *
     * @param url 头像的url地址
     */
    private void loadAvatar(String url) {
        if (!url.isEmpty()) {
            Glide.with(getContext()).load(url).into(ivAvatar);
        }
    }

    @OnClick({R.id.ly_personal_info, R.id.ly_my_income, R.id.ly_my_comment, R.id.ly_lesson_setup, R.id.ly_normal_setup})
    void doClick(View view) {
        Intent intent;
        switch (view.getId()) {
            // 个人资料
            case R.id.ly_personal_info:
                intent = new Intent(getActivity(), MyPersonalInfoActivity.class);
                startActivity(intent);
                break;
            // 我的收入
            case R.id.ly_my_income:
                intent = new Intent(getActivity(), MyIncomeActivity.class);
                startActivity(intent);
                break;
            // 我的评价
            case R.id.ly_my_comment:
                intent = new Intent(getActivity(), MyEvaluationActivity.class);
                startActivity(intent);
                break;
            // 课程设置
            case R.id.ly_lesson_setup:
                intent = new Intent(getActivity(), CourseSettingActivity.class);
                startActivity(intent);
                break;
            // 基本设置
            case R.id.ly_normal_setup:
                intent = new Intent(getActivity(), BasicSettingActivity.class);
                intent.putExtra("PHONE", phone);
                startActivity(intent);
                break;
        }
    }

}
