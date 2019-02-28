package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
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

public class MyTeachingExperiencesActivity extends AppCompatActivity {

    // 当前屏幕上可见的items数量 等于有多少条经历 最少为1
    int numOfVisLayout = 1;
    // list 用于存储教学经历
    ListOfEduExperience listOfEduExperience = new ListOfEduExperience();

    @BindViews({R.id.tv_begin_time, R.id.tv_begin_time2, R.id.tv_begin_time3,
            R.id.tv_begin_time4, R.id.tv_begin_time5})
    List<TextView> mBeginTimeList;
    @BindViews({R.id.tv_end_time, R.id.tv_end_time2, R.id.tv_end_time3,
            R.id.tv_end_time4, R.id.tv_end_time5})
    List<TextView> mEndTimeList;
    @BindViews({R.id.layout_experience, R.id.layout_experience2, R.id.layout_experience3,
            R.id.layout_experience4, R.id.layout_experience5})
    List<LinearLayout> experienceLayoutList;
    @BindViews({R.id.iv_remove, R.id.iv_remove2, R.id.iv_remove3, R.id.iv_remove4, R.id.iv_remove5})
    List<ImageView> removeIvList;
    @BindViews({R.id.et_experience, R.id.et_experience2, R.id.et_experience3,
            R.id.et_experience4, R.id.et_experience5})
    List<EditText> expEtList;
    @BindView(R.id.layout_add_experience)
    LinearLayout mLyAddExperience;
    @BindView(R.id.layout_remove_experience)
    LinearLayout mLyRemoveExperience;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teaching_experices);

        ButterKnife.bind(this);
        loadInfo();
    }

    /**
     * 从SharedPreferences中获取JSON并更新UI
     */
    private void loadInfo() {
        Observable.create((Observable.OnSubscribe<JSONArray>) subscriber -> {
            try {
                SharedPreferences userPreference = getSharedPreferences("setting", Activity.MODE_MULTI_PROCESS);
                String userInfo = userPreference.getString("info", "no_info");
                JSONArray infoJsonArray = new JSONObject(userInfo).getJSONArray("exp");
                KLog.json(userInfo);
                subscriber.onNext(infoJsonArray);
                subscriber.onCompleted();
            } catch (Exception e) {
                KLog.e(e);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JSONArray jsonArray) {
                        try {
                            // 获取有多少条经历 并设置对应数目的layout为可见
                            numOfVisLayout = jsonArray.length();
                            for (int i = 0; i < numOfVisLayout; i++) {
                                experienceLayoutList.get(i).setVisibility(View.VISIBLE);
                                listOfEduExperience.addExp(i, jsonArray.getJSONObject(i).getString("begintime").substring(0, 7),
                                        jsonArray.getJSONObject(i).getString("endtime").substring(0, 7),
                                        jsonArray.getJSONObject(i).getString("note"));
                                refreshAllLayout();
                            }

                            KLog.json(numOfVisLayout + "", jsonArray.toString());
                        } catch (Exception e) {
                            KLog.e(e);
                        }
                    }
                });
    }

    /**
     * 配置时间选择器
     */
    private TimePickerView initTimePicker(TextView beginTv) {

        return new TimePickerBuilder(this,
                (date, v) -> {
                    int year = date.getYear() + 1900;
                    int month = date.getMonth() + 1;
                    if (month < 10) beginTv.setText(year + "-0" + month);
                    else beginTv.setText(year + "-" + month);
                })
                .setType(new boolean[]{true, true, false, false, false, false})
                .build();
    }

    /**
     * 将屏幕上的数据打包成JSONArray
     * @return JSONArray 存储了所有教学经历
     */
    private JSONArray getNoteJsonArray() {
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

    /**
     * 判空
     */
    boolean isAllDone() {
        for (int i = 0; i < numOfVisLayout; i++) {
            if (mBeginTimeList.get(i).getText().toString().equals("开始时间")) return false;
            if (mEndTimeList.get(i).getText().toString().equals("结束时间")) return false;
            if (expEtList.get(i).getText().toString().isEmpty()) return false;
        }
        return true;
    }

    /**
     * 显示第一个对话框
     */
    private void showFirstDialog() {
        /*
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
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

    void updateInfo() {
        if (isAllDone()) {
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("exp", getNoteJsonArray());
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
                            KLog.json(responseBody.string());
                            showSecondDialog();
                        } catch (Exception e) {
                            KLog.e(e);
                        }
                    }
                });
            } catch (Exception e) {
                KLog.e(e);
            }
        }
        else {
            toastMessage("信息不完整");
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
                new AlertDialog.Builder(this);
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

    @OnClick({R.id.iv_back_arrow, R.id.tv_submit})
    void doAction(View view){
        switch(view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.tv_submit:
                refreshData();
                showFirstDialog();
                break;
        }
    }

    //============================================================================================
    //以下是操作教学经历动态增删列表的代码

    /**
     * 每个列表项的小删除按钮
     * The tiny Delete Button of each item in the list view
     */
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

    /**
     * 设定教学经历开始的时间
     * @param view
     */
    @OnClick({R.id.tv_begin_time, R.id.tv_begin_time2, R.id.tv_begin_time3, R.id.tv_begin_time4, R.id.tv_begin_time5})
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
        }
    }

    /**
     * 设定教学经历结束 的时间
     * @param view
     */
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

    /**
     * 页面底部的按钮 删除教学经历 添加教学经历
     * Buttons in the bottom of the page
     * Remove experience
     * Add experience
     */
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

    // 刷新数据listOfEduExperience
    // 将UI上的内容同步到数据列表
    // Refresh the data and store them into the listOfEduExperience
    // Get data from UI
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

    // 刷新所有的列表项
    // Refresh all layouts of items. set data to them
    private void refreshAllLayout() {
        for (int i = 0; i < 5; i++) {
            mBeginTimeList.get(i).setText(listOfEduExperience.get(i).getBeginTime());
            mEndTimeList.get(i).setText(listOfEduExperience.get(i).getEndTime());
            expEtList.get(i).setText(listOfEduExperience.get(i).getNote());
        }
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

    //=============================================================================================

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
