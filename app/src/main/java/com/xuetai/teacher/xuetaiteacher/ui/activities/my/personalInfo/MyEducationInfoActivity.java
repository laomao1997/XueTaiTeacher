package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.ListLevelGradeSubjectApi;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.view.EducationGradeBottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyEducationInfoActivity extends AppCompatActivity {

    String level;
    ArrayList<Integer> grade = new ArrayList<>();
    String subject;

    ArrayList<String> subjectListJunior = new ArrayList<>(); // 初中课程列表
    ArrayList<String> subjectListSenior = new ArrayList<>(); // 高中课程列表

    // 教授阶段选择控件
    OptionsPickerView stagePicker;

    @BindView(R.id.tv_submit)
    TextView mTvSubmit;

    @BindViews({R.id.ly_stage, R.id.ly_grade, R.id.ly_subject})
    List<RelativeLayout> relativeLayoutList;

    @BindViews({R.id.tv_stage, R.id.tv_grade, R.id.tv_subject})
    List<TextView> textViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_education_info);

        ButterKnife.bind(this);

        initDatas();
        loadInfo();
        initStagePicker();
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
                    JSONObject levelGradeSubjectJson = new JSONObject(resultArgs);
                    JSONArray levelJsonArray = levelGradeSubjectJson.getJSONObject("result").getJSONArray("level");
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

    // 从SharedPreferences中获取JSON并更新UI
    private void loadInfo() {
        Observable.create((Observable.OnSubscribe<JSONObject>) subscriber -> {
            try {
                SharedPreferences userPreference = getSharedPreferences("setting", Activity.MODE_MULTI_PROCESS);
                String userInfo = userPreference.getString("info", "no_info");
                JSONObject infoJson = new JSONObject(userInfo);
                KLog.json(userInfo);
                subscriber.onNext(infoJson);
                subscriber.onCompleted();
            } catch (Exception e) {
                KLog.e(e);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JSONObject infoJson) {
                        String stageArgs;
                        String gradeArgs;
                        String subjectArgs;
                        try {
                            stageArgs = infoJson.getString("level");
                            gradeArgs = infoJson.getString("grade");
                            subjectArgs = infoJson.getString("subject");
                            loadStage(stageArgs);
                            loadGrade(gradeArgs, stageArgs);
                            loadSubject(subjectArgs);
                        } catch (Exception e) {
                            KLog.e(e);
                        }
                    }
                });
    }

    /**
     * 保存服务器教学阶段数据到本地
     *
     * @param args 从服务器获得的字符串
     */
    private void loadStage(String args) {
        level = args;
        loadStageToTextView(level);
    }

    /**
     * 更新教学阶段UI
     *
     * @param args 教学阶段变量 1-初中 2-高中
     */
    private void loadStageToTextView(String args) {
        if ("1".equals(args)) args = "初中";
        else args = "高中";
        textViewList.get(0).setText(args);
    }

    private void loadGrade(String args, String level) {
        ArrayList<Integer> gradeList = new ArrayList<>();

        switch (args.length()) {
            case 3:
                gradeList.add(Integer.parseInt(args.charAt(1) + ""));
                break;
            case 5:
                gradeList.add(Integer.parseInt(args.charAt(1) + ""));
                gradeList.add(Integer.parseInt(args.charAt(3) + ""));
                break;
            case 7:
                gradeList.add(Integer.parseInt(args.charAt(1) + ""));
                gradeList.add(Integer.parseInt(args.charAt(3) + ""));
                gradeList.add(Integer.parseInt(args.charAt(5) + ""));
                break;
        }
        for (int i : gradeList) {
            grade.add(i);
        }
        loadGradeToTextView(grade);
    }

    private void loadGradeToTextView(ArrayList<Integer> gradeList) {
        StringBuilder gradesText = new StringBuilder();
        if (gradeList.size() != 3) {
            for (int i : gradeList) {
                switch (i) {
                    case 1:
                        gradesText.append("初一");
                        break;
                    case 2:
                        gradesText.append("初二");
                        break;
                    case 3:
                        gradesText.append("初三");
                        break;
                    case 4:
                        gradesText.append("高一");
                        break;
                    case 5:
                        gradesText.append("高二");
                        break;
                    case 6:
                        gradesText.append("高三");
                        break;
                }
            }
            if ((gradeList.size() != 1) && (gradeList.size() != 0)) gradesText.insert(2, "、");
        } else {
            if (level.equals("1")) gradesText.append("初").append("一、").append("初").append("二、").append("初").append("三");
            else gradesText.append("高").append("一、").append("高").append("二、").append("高").append("三");
        }
        textViewList.get(1).setText(gradesText);
    }

    private void loadSubject(String args) {
        subject = args;
        loadSubjectToTextView(subject);
    }

    private void loadSubjectToTextView(String args) {
        switch (args) {
            case "2":
                args = "数学";
                break;
            case "3":
                args = "英语";
                break;
            case "4":
                args = "物理";
                break;
            case "5":
                args = "化学";
                break;
            case "6":
                args = "生物";
                break;
            case "8":
                args = "地理";
                break;
            case "1":
                args = "语文";
                break;
            case "7":
                args = "历史";
                break;
        }
        textViewList.get(2).setText(args);
    }

    /**
     * 配置教授阶段选择picker
     */
    private void initStagePicker() {
        ArrayList<String> stageList = new ArrayList<>();
        stageList.add("初中");
        stageList.add("高中");
        stagePicker = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            if (!level.equals((options1 + 1) + "")) {
                level = (options1 + 1) + "";
                grade.clear();
                subject = "";
                loadGradeToTextView(grade);
                loadSubjectToTextView(subject);
            }
            loadStageToTextView(level);
        }).build();

        stagePicker.setPicker(stageList);
        stagePicker.setTitleText("请选择教授阶段");
    }

    /**
     * 教学年级选择对话框
     *
     * @param levelArgs 教学阶段参数 1-初 2-高
     */
    private void showMultiChoiceDialog(String levelArgs) {
        System.out.println(grade);
        if ("1".equals(levelArgs)) levelArgs = "初";
        else levelArgs = "高";
        boolean[] itemsCheck = {false, false, false};
        final String[] items = {levelArgs + "一", levelArgs + "二", levelArgs + "三"};
        // 设置默认选中的选项，为false则默认未选中
        final boolean initChoiceSets[] = {false, false, false};
        for (int i : grade) {
            switch (i) {
                case 1:
                    itemsCheck[0] = true;
                    initChoiceSets[0] = true;
                    break;
                case 2:
                    itemsCheck[1] = true;
                    initChoiceSets[1] = true;
                    break;
                case 3:
                    itemsCheck[2] = true;
                    initChoiceSets[2] = true;
                    break;
            }
        }

        AlertDialog.Builder multiChoiceDialog =
                new AlertDialog.Builder(MyEducationInfoActivity.this);
        multiChoiceDialog.setTitle("请选择教学年级");
        multiChoiceDialog.setMultiChoiceItems(items, initChoiceSets,
                (dialog, which, isChecked) -> {
                    if (isChecked) {
                        itemsCheck[which] = true;
                    } else {
                        itemsCheck[which] = false;
                    }
                });
        multiChoiceDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    grade.clear();
                    int size = itemsCheck.length;
                    for (int i = 0; i < size; i++) {
                        if (itemsCheck[i]) {
                            if (this.level.equals("1")) grade.add(i + 1);
                            else grade.add(i + 4);
                        }
                    }
                    if (this.level.equals("1")) loadGradeToTextView(grade);
                    else loadGradeToTextView(grade);
                });
        multiChoiceDialog.show();
    }

    /**
     * 教学科目选择器
     *
     * @param levelArgs 教学阶段参数 1-初中 2-高中
     */
    String subjectChosen;

    private void showSingleChoiceDialog(String levelArgs) {
        subjectChosen = "";
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(MyEducationInfoActivity.this);
        singleChoiceDialog.setTitle("请选择教学科目");
        if ("1".equals(levelArgs)) {
            final String[] items1 = {"数学", "英语", "物理", "化学", "生物"};
            // 第二个参数是默认选项，此处设置为0
            singleChoiceDialog.setSingleChoiceItems(items1, 0,
                    (dialog, which) -> subjectChosen = items1[which]);
        } else {
            final String[] items2 = {"数学", "英语", "物理", "化学", "生物", "地理", "语文", "历史"};
            // 第二个参数是默认选项，此处设置为0
            singleChoiceDialog.setSingleChoiceItems(items2, 0,
                    (dialog, which) -> subjectChosen = items2[which]);
        }

        singleChoiceDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    switch (subjectChosen) {
                        case "数学":
                            subject = "2";
                            break;
                        case "英语":
                            subject = "3";
                            break;
                        case "物理":
                            subject = "4";
                            break;
                        case "化学":
                            subject = "5";
                            break;
                        case "生物":
                            subject = "6";
                            break;
                        case "地理":
                            subject = "8";
                            break;
                        case "语文":
                            subject = "1";
                            break;
                        case "历史":
                            subject = "7";
                            break;
                    }
                    loadSubjectToTextView(subject);
                });
        singleChoiceDialog.show();
    }

    private void updateInfo() {
        System.out.println(level);
        System.out.println(grade);
        System.out.println(subject);
        JSONObject jsonParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int g : grade) jsonArray.put(g);
        try {
            jsonParams.put("level", level);
            jsonParams.put("grade", jsonArray);
            jsonParams.put("subject", subject);
//            KLog.json("测试json", jsonParams.toString());
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.UpDateInfo, jsonParams, new Subscriber<ResponseBody>() {
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
//                    KLog.json("请求结果", resultArgs);
                    showSecondDialog();
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    /**
     * 显示第一个对话框
     */
    private void showFirstDialog() {
        // 判空
        if (level.isEmpty() || grade.isEmpty() || subject.isEmpty()) {
            toastMessage("信息不完整 请重新填写");
        } else {
            /*
             * @setTitle 设置对话框标题
             * @setMessage 设置对话框消息提示
             * setXXX方法返回Dialog对象，因此可以链式设置属性
             */
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(MyEducationInfoActivity.this);
            normalDialog.setTitle("提交")// 设置标题
                    .setMessage("本页面新提交的资料提交后需要工作人员审核通过后才展示，是否确认提交？") // 设置文本内容
                    .setPositiveButton("确定提交",
                            (dialog, which) -> {
                                updateInfo();//...To-do
                            })
                    .setNegativeButton("关闭",
                            (dialog, which) -> {
                                //...To-do
                            })
                    .show();// 显示
        }
    }

    /**
     * 显示第二个对话框
     */
    private void showSecondDialog() {
        /*
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyEducationInfoActivity.this);
        normalDialog.setTitle("提交完成"); // 设置标题
        normalDialog.setMessage("请等待客服审核资料。审核通过前您的资料不会有变化。"); // 设置文本内容
        normalDialog.setNegativeButton("关闭",
                (dialog, which) -> {
                    //...To-do
                    finish();
                });
        // 显示
        normalDialog.show();
    }

    @OnClick({R.id.iv_back_arrow, R.id.tv_submit, R.id.ly_stage, R.id.ly_grade, R.id.ly_subject})
    void doDealWithThem(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.tv_submit:
                showFirstDialog();
                break;
            case R.id.ly_stage:
                stagePicker.show();
                setSubmitButtonEnable();
                break;
            case R.id.ly_grade:
                showMultiChoiceDialog(level);
                setSubmitButtonEnable();
                break;
            case R.id.ly_subject:
                showSingleChoiceDialog(level);
                setSubmitButtonEnable();
                break;
        }
    }

    // 将提交按钮设为可用
    void setSubmitButtonEnable() {
        mTvSubmit.setTextColor(Color.BLACK);
        mTvSubmit.setEnabled(true);
    }

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
