package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.ListLevelGradeSubjectApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadInfoApi;
import com.xuetai.teacher.xuetaiteacher.view.EducationGradeBottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Jinghao Zhang on 2018/12/07
 * 这个类有点难懂我知道
 * 由于没有采用MVP架构，所以这个类是Controller也是Model (无奈)
 * 恰好产品学长设计了一些个难以用ListView实现的requirements
 * 因此没办法，有大量的代码都用来操作view及其相关的业务
 */
public class EducationInfoActivity extends AppCompatActivity {

    int numOfVisLayout = 1;

    JSONObject levelGradeSubjectJson;

    ArrayList<String> stageList = new ArrayList<>(); // 教学阶段 - 初中/高中
    ArrayList<String> subjectListJunior = new ArrayList<>(); // 初中课程列表
    ArrayList<String> subjectListSenior = new ArrayList<>(); // 高中课程列表

    String stagePicked = "";
    String subjectPicked = "";

    String mStagePicked;
    ArrayList<Integer> mGradesPicked = new ArrayList<>();
    String mSubjectPicked;

    // 教授阶段选择控件
    OptionsPickerView stagePicker;
    // 科目选择控件
    OptionsPickerView subjectPicker;

    ListOfEduExperience listOfEduExperience = new ListOfEduExperience();

    EducationGradeBottomSheetDialog educationGradeBottomSheetDialog;

    @BindView(R.id.tv_stage)
    TextView mStageTextView;

    @BindView(R.id.tv_grades)
    TextView mGradesTextView;

    @BindView(R.id.tv_subject)
    TextView mSubjectTextView;

    @BindViews({R.id.tv_begin_time, R.id.tv_begin_time2, R.id.tv_begin_time3, R.id.tv_begin_time4, R.id.tv_begin_time5})
    List<TextView> mBeginTimeList;

    @BindViews({R.id.tv_end_time, R.id.tv_end_time2, R.id.tv_end_time3, R.id.tv_end_time4, R.id.tv_end_time5})
    List<TextView> mEndTimeList;

    @BindViews({R.id.layout_experience, R.id.layout_experience2, R.id.layout_experience3, R.id.layout_experience4, R.id.layout_experience5})
    List<LinearLayout> experienceLayoutList;

    @BindViews({R.id.iv_remove, R.id.iv_remove2, R.id.iv_remove3, R.id.iv_remove4, R.id.iv_remove5})
    List<ImageView> removeIvList;

    @BindViews({R.id.et_experience, R.id.et_experience2, R.id.et_experience3, R.id.et_experience4, R.id.et_experience5})
    List<EditText> expEtList;

    @BindView(R.id.layout_add_experience)
    LinearLayout mLyAddExperience;

    @BindView(R.id.layout_remove_experience)
    LinearLayout mLyRemoveExperience;

    @BindView(R.id.layout_subject)
    RelativeLayout mLySubject;

    @BindView(R.id.layout_education_grade)
    RelativeLayout mLyGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_info);

        ButterKnife.bind(this);

        mLyRemoveExperience.setEnabled(false);

        initDatas();
        initStagePicker();
//        initSubjectPicker();
        mLySubject.setEnabled(false);
        mLyGrade.setEnabled(false);

    }

    private void initDatas() {
        ListLevelGradeSubjectApi.getInstance().uploadResult(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String resultArgs = responseBody.string();
                    System.out.println(resultArgs);
                    KLog.json(resultArgs);
                    levelGradeSubjectJson = new JSONObject(resultArgs);
                    JSONArray levelJsonArray = levelGradeSubjectJson.getJSONObject("result").getJSONArray("level");
                    for (int i = 0; i < levelJsonArray.length(); i++) {
                        stageList.add(new JSONObject(levelJsonArray.get(i).toString()).getString("value"));
                    }
                    JSONArray juniorJsonArray = new JSONObject(levelJsonArray.get(0).toString()).getJSONArray("subject");
                    for (int i = 0; i < juniorJsonArray.length(); i++) {
                        subjectListJunior.add(new JSONObject(juniorJsonArray.get(i).toString()).getString("value"));
                    }
                    JSONArray seniorJsonArray = new JSONObject(levelJsonArray.get(1).toString()).getJSONArray("subject");
                    for (int i = 0; i < seniorJsonArray.length(); i++) {
                        subjectListSenior.add(new JSONObject(seniorJsonArray.get(i).toString()).getString("value"));
                    }
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    /**
     * 配置教授阶段选择picker
     */
    private void initStagePicker() {
        stagePicker = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            stagePicked = stageList.get(options1);
            mStageTextView.setText(stagePicked);
            if ("初中".equals(stagePicked)) {
                initSubjectPicker(subjectListJunior);
            } else {
                initSubjectPicker(subjectListSenior);
            }
            mStageTextView.setTextColor(Color.rgb(48, 48, 48));
            mLyGrade.setEnabled(true);
            mLySubject.setEnabled(true);
            educationGradeBottomSheetDialog = new EducationGradeBottomSheetDialog();
        }).build();

        stagePicker.setPicker(stageList);
        stagePicker.setTitleText("请选择教授阶段");

    }

    /**
     * 配置科目选择picker
     */
    private void initSubjectPicker(ArrayList<String> subjectList) {

        subjectPicker = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            subjectPicked = subjectList.get(options1);
            mSubjectTextView.setText(subjectPicked);
            mSubjectTextView.setTextColor(Color.rgb(48, 48, 48));
        }).build();

        subjectPicker.setPicker(subjectList);
        subjectPicker.setTitleText("请选择教学科目");
    }

    /**
     * 配置时间选择器
     */
    private TimePickerView initTimePicker(TextView beginTv) {

        return new TimePickerBuilder(this,
                (date, v) -> beginTv.setText((date.getYear() + 1900) + "-" + (date.getMonth() + 1)))
                .setType(new boolean[]{true, true, false, false, false, false})
                .build();
    }

    private void refreshAllLayout() {
        for (int i = 0; i < 5; i++) {
            mBeginTimeList.get(i).setText(listOfEduExperience.get(i).getBeginTime());
            mEndTimeList.get(i).setText(listOfEduExperience.get(i).getEndTime());
            expEtList.get(i).setText(listOfEduExperience.get(i).getNote());
        }
    }

    @OnClick(R.id.tv_next_step_button)
    void doNextStep() {
        refreshData();
        if (isAllInfoValid()) {
            switch (stagePicked) {
                case "初中":
                    mStagePicked = "1";
                    break;
                case "高中":
                    mStagePicked = "2";
                    break;
            }
            switch (subjectPicked) {
                case "数学":
                    mSubjectPicked = "2";
                    break;
                case "英语":
                    mSubjectPicked = "3";
                    break;
                case "物理":
                    mSubjectPicked = "4";
                    break;
                case "化学":
                    mSubjectPicked = "5";
                    break;
                case "生物":
                    mSubjectPicked = "6";
                    break;
                case "地理":
                    mSubjectPicked = "8";
                    break;
                case "语文":
                    mSubjectPicked = "1";
                    break;
                case "历史":
                    mSubjectPicked = "1";
                    break;
            }
            JSONObject jsonParams = new JSONObject();
            JSONArray jsonArray = uploadNoteJsonObject();
            JSONArray gradeArray = new JSONArray();
            for (int grade : mGradesPicked) gradeArray.put(grade);
            try {
                jsonParams.put("level", mStagePicked);
                jsonParams.put("grade", gradeArray);
                jsonParams.put("subject", mSubjectPicked);
                jsonParams.put("exp", jsonArray);
//            KLog.json(jsonParams.toString());
            } catch (Exception e) {
                KLog.e(e);
            }
            UploadInfoApi.getInstance().uploadResult(jsonParams, new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    try {
                        String resultArgs = responseBody.string();
                        System.out.println(resultArgs);
                        KLog.json(resultArgs);
                        Intent intent = new Intent(EducationInfoActivity.this, DiplomaIndentifyActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        KLog.e(e);
                    }
                }
            });
        }
    }

    @OnClick(R.id.layout_education_stage)
    public void doSetStage() {
        mLyGrade.setEnabled(false);
        mGradesTextView.setText("");
        mLySubject.setEnabled(false);
        mSubjectTextView.setText("");
        stagePicker.show();
    }

    // 年级选择器 使用自定义对话框
    @OnClick(R.id.layout_education_grade)
    public void doSetGrade() {
        mGradesPicked.clear();
        if (!stagePicked.equals("")) {
            educationGradeBottomSheetDialog.show(getSupportFragmentManager(), "GradeDialog");
            educationGradeBottomSheetDialog.setClicklistener(() -> {
                mGradesTextView.setText(educationGradeBottomSheetDialog.getSelected());
                mGradesTextView.setTextColor(Color.rgb(48, 48, 48));
                for (String s : educationGradeBottomSheetDialog.getGradeSelectedList()) {
                    mGradesPicked.add(Integer.parseInt(s));
                }
            });
        }
    }

    @OnClick(R.id.layout_subject)
    public void doSetSubject() {
        subjectPicker.show();
    }

    @OnClick({R.id.iv_remove, R.id.iv_remove2, R.id.iv_remove3, R.id.iv_remove4, R.id.iv_remove5})
    void doRemoveButton(View view) {
        switch (view.getId()) {
            case R.id.iv_remove: {
                listOfEduExperience.removeExp(0);
                refreshAllLayout();
                break;
            }
            case R.id.iv_remove2: {
                listOfEduExperience.removeExp(1);
                refreshAllLayout();
                break;
            }
            case R.id.iv_remove3: {
                listOfEduExperience.removeExp(2);
                refreshAllLayout();
                break;
            }
            case R.id.iv_remove4: {
                listOfEduExperience.removeExp(3);
                refreshAllLayout();
                break;
            }
            case R.id.iv_remove5: {
                listOfEduExperience.removeExp(4);
                refreshAllLayout();
                break;
            }
        }
        if (numOfVisLayout > 1) {
            numOfVisLayout--;
            for (int i = 4; i > numOfVisLayout - 1; i--) {
                experienceLayoutList.get(i).setVisibility(View.GONE);
            }
        }
        mLyAddExperience.setEnabled(true);
        for (int i = 0; i < 5; i++) {
            removeIvList.get(i).setVisibility(View.GONE);
        }
        if (numOfVisLayout == 1) mLyRemoveExperience.setEnabled(false);
    }

    // 页面底部的按钮 删除教学经历 添加教学经历
    @OnClick({R.id.layout_add_experience, R.id.layout_remove_experience})
    void doBottomButton(View view) {
        refreshData();
        switch (view.getId()) {
            case R.id.layout_add_experience: {
                if (numOfVisLayout < 5) {
                    numOfVisLayout++;
                    for (int i = 0; i < numOfVisLayout; i++) {
                        experienceLayoutList.get(i).setVisibility(View.VISIBLE);
                    }
                    if (numOfVisLayout == 5) {
                        toastMessage("最多只能添加五条教学经历");
                        mLyAddExperience.setEnabled(false);
                    }
                }
                mLyRemoveExperience.setEnabled(true);
            }
            break;
            case R.id.layout_remove_experience: {
                for (int i = 0; i < 5; i++) {
                    removeIvList.get(i).setVisibility(View.VISIBLE);
                }
                break;
            }
        }

    }

    // 设定教学经历开始的时间
    @OnClick({R.id.tv_begin_time, R.id.tv_begin_time2, R.id.tv_begin_time3,
            R.id.tv_begin_time4, R.id.tv_begin_time5, R.id.iv_back_arrow})
    void doSetBeginTime(View view) {
        switch (view.getId()) {
            case R.id.tv_begin_time: {
                initTimePicker(mBeginTimeList.get(0)).show();
                break;
            }
            case R.id.tv_begin_time2: {
                initTimePicker(mBeginTimeList.get(1)).show();

                break;
            }
            case R.id.tv_begin_time3: {
                initTimePicker(mBeginTimeList.get(2)).show();

                break;
            }
            case R.id.tv_begin_time4: {
                initTimePicker(mBeginTimeList.get(3)).show();

                break;
            }
            case R.id.tv_begin_time5: {
                initTimePicker(mBeginTimeList.get(4)).show();

                break;
            }
            case R.id.iv_back_arrow:
                finish();
                break;
        }
    }

    // 设定教学经历结束 的时间
    @OnClick({R.id.tv_end_time, R.id.tv_end_time2, R.id.tv_end_time3, R.id.tv_end_time4, R.id.tv_end_time5})
    void doSetEndTime(View view) {
        switch (view.getId()) {
            case R.id.tv_end_time: {
                initTimePicker(mEndTimeList.get(0)).show();

                break;
            }
            case R.id.tv_end_time2: {
                initTimePicker(mEndTimeList.get(1)).show();

                break;
            }
            case R.id.tv_end_time3: {
                initTimePicker(mEndTimeList.get(2)).show();

                break;
            }
            case R.id.tv_end_time4: {
                initTimePicker(mEndTimeList.get(3)).show();

                break;
            }
            case R.id.tv_end_time5: {
                initTimePicker(mEndTimeList.get(4)).show();

                break;
            }
        }
    }

    public String getStagePicked() {
        return stagePicked;
    }

    private void refreshData() {
        for (int i = 0; i < 5; i++) {
            listOfEduExperience.addExp(i,
                    mBeginTimeList.get(i).getText().toString(),
                    mEndTimeList.get(i).getText().toString(),
                    expEtList.get(i).getText().toString());
            listOfEduExperience.addExp(i,
                    mBeginTimeList.get(i).getText().toString(),
                    mEndTimeList.get(i).getText().toString(),
                    expEtList.get(i).getText().toString());
        }
        System.out.println("当前内容为\n" + listOfEduExperience.toString());
    }

    private JSONArray uploadNoteJsonObject() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < numOfVisLayout; i++) {
                JSONObject jjj = new JSONObject();
                jjj.put("begintime", mBeginTimeList.get(i).getText().toString() + "-01T00:00:00.000Z");
                jjj.put("endtime", mEndTimeList.get(i).getText().toString() + "-01T00:00:00.000Z");
                jjj.put("note", expEtList.get(i).getText().toString());
                jsonArray.put(jjj);
            }
        } catch (Exception e) {
            KLog.e(e);
        }
        return jsonArray;
    }

    boolean isAllInfoValid() {
        if (stagePicked.isEmpty() || subjectPicked.isEmpty() || mGradesPicked.isEmpty()) {
            toastMessage("信息不完整");
            return false;
        }
        return true;
    }

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 存储教学经历的BEAN
     * beginTime - 开始时间
     * endTime - 结束时间
     * note - 教学经历
     */
    class EduExperience {
        String beginTime;
        String endTime;
        String note;

        EduExperience(String beginTime, String endTime, String note) {
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.note = note;
        }

        String getBeginTime() {
            if ("".equals(beginTime)) return "开始时间";
            return beginTime;
        }

        void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        String getEndTime() {
            if ("".equals(endTime)) return "结束时间";
            return endTime;
        }

        void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        String getNote() {
            return note;
        }

        void setNote(String note) {
            this.note = note;
        }

        @NonNull
        @Override
        public String toString() {
            return "开始时间:" + beginTime + " 结束时间:" + endTime + " 教学经历:" + note + "\n";
        }
    }

    @OnTextChanged({R.id.tv_end_time, R.id.tv_end_time2, R.id.tv_end_time3, R.id.tv_end_time4, R.id.tv_end_time5,
            R.id.tv_begin_time, R.id.tv_begin_time2, R.id.tv_begin_time3, R.id.tv_begin_time4, R.id.tv_begin_time5})
    void doOnTextChanged() {
        //System.out.println("当前内容为\n" + listOfEduExperience.toString());
    }

    /**
     * 操作教学经历beans的对象
     */
    class ListOfEduExperience {

        List<EduExperience> eduList = new ArrayList<>();
        int num = 5;

        ListOfEduExperience() {
            for (int i = 0; i < num; i++) {
                eduList.add(new EduExperience("", "", ""));
            }
        }

        void addExp(int index, String begin, String end, String note) {
            eduList.get(index).setBeginTime(begin);
            eduList.get(index).setEndTime(end);
            eduList.get(index).setNote(note);
        }

        void removeExp(int index) {
            eduList.remove(index);
            eduList.add(new EduExperience("", "", ""));
        }

        public EduExperience get(int index) {
            return eduList.get(index);
        }

        @NonNull
        @Override
        public String toString() {
            return eduList.get(0).toString() + eduList.get(1).toString() + eduList.get(2).toString()
                    + eduList.get(3).toString() + eduList.get(4).toString() + "\n" + num;
        }
    }

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

}
