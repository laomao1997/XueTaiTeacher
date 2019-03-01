package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.ui.activities.course.SetTutorialContentActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.course.ViewSelectedTutorilActivity;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * {
 * "timeDesc": "今天21:00-22:55 (2课时)",
 * "courseware": false, 是否已备课
 * "coach": false, 是否已经设置了辅导内容
 * "cancel": false, 如果已经进行了退改课，则返回退改课的信息
 * "type": "1", 1:一对一，2：系列1对1（暂时没有），3：专题课
 * "id": "1833",
 * "photos": null,
 * "displayVersion": null,
 * "title": null,
 * "price": "158元",
 * "student": {
 * "phone": "13333333333",
 * "school": "",
 * "level": "初中",
 * "studentId": "1091",
 * "name": "谁啊"
 * },
 * "description": null,
 * "courseStatus": "0", 0教室未开放 1正在上课 2正常下课 3教室开放 4课程异常结束
 * "secret": 610567652015,
 * "comment": null,
 * "displayGrade": null
 * }
 */

public class SetLessonActivity extends AppCompatActivity {

    String id;

    String nickname;
    String phone;
    String school;
    String level;
    String time;
    String score;
    String commentName;
    String comment;
    String commentTime;
    String reply = "";
    String courseStatus;
    boolean courseware = false;
    boolean coach = false;
    String type;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindViews({R.id.tv_nick_name, R.id.tv_phone, R.id.tv_school, R.id.tv_grade, R.id.tv_time,
            R.id.tv_coach, R.id.tv_courseware, R.id.tv_courseStatus, R.id.tv_comment})
    List<SuperTextView> superTextViews;

    @BindView(R.id.ly_comment)
    ConstraintLayout layoutComment;
    @BindView(R.id.iv_avatar)
    ImageView ivScore;
    @BindView(R.id.tv_studnet_name)
    TextView tvStuName;
    @BindView(R.id.tv_date)
    TextView tvDateComment;
    @BindView(R.id.tv_evaluation)
    TextView tvEvaluation;
    @BindView(R.id.tv_reply)
    TextView tvReply;
    @BindView(R.id.ly_reply)
    LinearLayout layoutReply;

    @BindView(R.id.tv_btn_reply)
    TextView tvBtnReply;
    @BindView(R.id.iv_btn_reply)
    ImageView ivBtnReply;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lesson);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        initData(intent.getStringExtra("LESSON_DETAIL"));

    }

    void initData(String jsonString) {
        KLog.json(jsonString);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        try {
            id = jsonObject.getString("id");
            time = jsonObject.getString("timeDesc");
            courseStatus = jsonObject.getString("courseStatus");
            courseware = jsonObject.getBoolean("courseware");
            coach = jsonObject.getBoolean("coach");
            type = jsonObject.getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            nickname = jsonObject.getJSONObject("student").getString("name");
            phone = jsonObject.getJSONObject("student").getString("phone");
            school = jsonObject.getJSONObject("student").getString("school");
            level = jsonObject.getJSONObject("student").getString("level");
        } catch (Exception e) {
            e.printStackTrace();
            nickname = "";
            phone = "";
            school = "";
            level = "";
        }
        try {
            score = jsonObject.getJSONObject("comment").getString("score");
            commentName = jsonObject.getJSONObject("comment").getString("name");
            comment = jsonObject.getJSONObject("comment").getString("comment");
            commentTime = jsonObject.getJSONObject("comment").getString("commentTime");
        } catch (Exception e) {
            e.printStackTrace();
            score = "";
            commentName = "";
            comment = "";
            commentTime = "";
        }
        try {
            reply = jsonObject.getJSONObject("comment").getString("reply");
            System.out.println(reply);
        } catch (Exception e) {
            e.printStackTrace();
            reply = "";
        }
        refreshView();
    }

    void refreshView() {
        if (courseStatus.equals("2")) tvTitle.setText("课程详情");
        superTextViews.get(0).setRightString(nickname);
        superTextViews.get(1).setRightString(phone);
        superTextViews.get(2).setRightString(school);
        superTextViews.get(3).setRightString(level);
        superTextViews.get(4).setRightString(time);

        if (coach) superTextViews.get(5).setRightString("查看");
        if (courseware) superTextViews.get(6).setRightString("请在PC端查看");
        switch (courseStatus) {
            case "1":
                superTextViews.get(7).setRightString("正在上课");
                break;
            case "2":
                superTextViews.get(7).setRightString("查看回放");
                break;
            case "3":
                superTextViews.get(7).setRightString("异常结束");
                break;
            default:
                superTextViews.get(7).setRightIcon(null);
                break;
        }
        if (!comment.isEmpty()) {
            superTextViews.get(8).setRightString("");
            switch (score) {
                case "3":
                    ivScore.setImageResource(R.drawable.ic_good_comment);
                    break;
                case "2":
                    ivScore.setImageResource(R.drawable.ic_medium_comment);
                    break;
                case "1":
                    ivScore.setImageResource(R.drawable.ic_bad_comment);
                    break;
            }
            tvStuName.setText(nickname);
            tvDateComment.setText(commentTime);
            tvEvaluation.setText(comment);
            if (!(reply == null)) {
                tvBtnReply.setVisibility(View.GONE);
                ivBtnReply.setVisibility(View.GONE);
                tvReply.setText(reply);
            } else {
                layoutReply.setVisibility(View.GONE);
            }
        } else {
            layoutComment.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_nick_name, R.id.tv_phone, R.id.tv_school, R.id.tv_grade, R.id.tv_time,
            R.id.tv_message, R.id.tv_comment, R.id.tv_coach})
    void doDealWithThem(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_nick_name:
                break;
            case R.id.tv_phone:
                break;
            case R.id.tv_school:
                break;
            case R.id.tv_grade:
                break;
            case R.id.tv_time:
                break;
            case R.id.tv_message:
                break;
            case R.id.tv_comment:
                break;
            case R.id.tv_coach:
                if (!coach) {
                    intent = new Intent(SetLessonActivity.this, SetTutorialContentActivity.class);
                    intent.putExtra("ID", id);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(SetLessonActivity.this, ViewSelectedTutorilActivity.class);
                    intent.putExtra("ID", id);
                    startActivity(intent);
                }
                break;
        }
    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }
}
