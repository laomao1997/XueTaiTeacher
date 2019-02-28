package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class MyHonorActivity extends AppCompatActivity {

    // 当前屏幕上可见的items数量 等于有多少条经历 最少为1
    int numOfVisLayout = 1;
    // list 用于存储教学经历
    ListOfHonor listOfHonor = new ListOfHonor();

    @BindViews({R.id.layout_honor1, R.id.layout_honor2, R.id.layout_honor3,
            R.id.layout_honor4, R.id.layout_honor5})
    List<RelativeLayout> honorLayoutList;
    @BindViews({R.id.iv_remove1, R.id.iv_remove2, R.id.iv_remove3, R.id.iv_remove4, R.id.iv_remove5})
    List<ImageView> removeIvList;
    @BindViews({R.id.tv_writed1, R.id.tv_writed2, R.id.tv_writed3,
            R.id.tv_writed4, R.id.tv_writed5})
    List<TextView> writedTvList;
    @BindView(R.id.layout_add_experience)
    LinearLayout mLyAddExperience;
    @BindView(R.id.layout_remove_experience)
    LinearLayout mLyRemoveExperience;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_honor_identification);

        ButterKnife.bind(this);

        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                listOfHonor.addHonor(requestCode - 1,
                        data.getExtras().getString("honor_result", ""),
                        data.getExtras().getString("honor_url", ""));
            } catch (Exception e) {
                KLog.e(e);
            }
            refreshLayout();
            System.out.println(listOfHonor);
        }
    }

    /**
     * 从 SharedPreferences 获得数据并加载到UI
     */
    void initData() {
        try {
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, "setting");
            String userInfo = sharedPreferencesHelper.getSharedPreference("info", "no_info").toString();
            JSONArray infoJsonArray = new JSONObject(userInfo).getJSONArray("cert");
            numOfVisLayout = infoJsonArray.length();
            if (numOfVisLayout != 0) {
                for (int i = 0; i < infoJsonArray.length(); i++) {
                    listOfHonor.get(i).setNote(infoJsonArray.getJSONObject(i).getString("note"));
                    listOfHonor.get(i).setUrl(infoJsonArray.getJSONObject(i).getString("photo"));
                }
            }
            refreshLayout();
            KLog.json(userInfo);
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    private JSONObject getHonorJson() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < numOfVisLayout; i++) {
                JSONObject json = new JSONObject();
                json.put("note", listOfHonor.get(i).getNote());
                json.put("photo", listOfHonor.get(i).getUrl());
                jsonArray.put(json);
            }
            jsonObject.put("cert", jsonArray);
        } catch (Exception e) {
            KLog.e(e);
        }
        return jsonObject;
    }

    @OnClick({R.id.tv_submit, R.id.iv_back_arrow})
    void doSubmit(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                showFirstDialog();
                break;
            case R.id.iv_back_arrow:
                finish();
                break;
        }
    }

    // 点击每个item打开新的activity
    @OnClick({R.id.layout_honor1, R.id.layout_honor2, R.id.layout_honor3, R.id.layout_honor4, R.id.layout_honor5})
    void doOpenSetHonorActivity(View view) {
        Intent intent = new Intent(MyHonorActivity.this, MyWriteHonorActivity.class);
        switch (view.getId()) {
            case R.id.layout_honor1:
                intent.putExtra("honor_result", listOfHonor.get(0).getNote());
                intent.putExtra("honor_url", listOfHonor.get(0).getUrl());
                startActivityForResult(intent, 1);
                break;
            case R.id.layout_honor2:
                intent.putExtra("honor_result", listOfHonor.get(1).getNote());
                intent.putExtra("honor_url", listOfHonor.get(1).getUrl());
                startActivityForResult(intent, 2);
                break;
            case R.id.layout_honor3:
                intent.putExtra("honor_result", listOfHonor.get(2).getNote());
                intent.putExtra("honor_url", listOfHonor.get(2).getUrl());
                startActivityForResult(intent, 3);
                break;
            case R.id.layout_honor4:
                intent.putExtra("honor_result", listOfHonor.get(3).getNote());
                intent.putExtra("honor_url", listOfHonor.get(3).getUrl());
                startActivityForResult(intent, 4);
                break;
            case R.id.layout_honor5:
                intent.putExtra("honor_result", listOfHonor.get(4).getNote());
                intent.putExtra("honor_url", listOfHonor.get(4).getUrl());
                startActivityForResult(intent, 5);
                break;
        }
    }

    private boolean isHonorFinished() {
        for (int i = 0; i < numOfVisLayout; i++) {
            if (listOfHonor.get(i).getNote().isEmpty()) return false;
        }
        return true;
    }

    /**
     * 显示第一个对话框
     */
    private void showFirstDialog() {
        // 判空
        if (!isHonorFinished()) {
            toastMessage("信息不完整 请重新填写");
        } else {
            /*
             * @setTitle 设置对话框标题
             * @setMessage 设置对话框消息提示
             * setXXX方法返回Dialog对象，因此可以链式设置属性
             */
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(MyHonorActivity.this);
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
     * 将所有信息上传到服务器 等待审核
     */
    private void updateInfo() {
        NormalApi.getInstance().getResult(MethodCode.UpDateInfo, getHonorJson(), new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                showSecondDialog();
            }
        });
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
                new AlertDialog.Builder(MyHonorActivity.this);
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


    //============================================================================================
    //以下是操作教学经历动态增删列表的代码

    /**
     * 每个列表项的小删除按钮
     * The tiny Delete Button of each item in the list view
     */
    @OnClick({R.id.iv_remove1, R.id.iv_remove2, R.id.iv_remove3, R.id.iv_remove4, R.id.iv_remove5})
    void doRemoveButton(View view) {
        switch (view.getId()) {
            case R.id.iv_remove1: {
                listOfHonor.removeHonor(0);
                break;
            }
            case R.id.iv_remove2: {
                listOfHonor.removeHonor(1);
                break;
            }
            case R.id.iv_remove3: {
                listOfHonor.removeHonor(2);
                break;
            }
            case R.id.iv_remove4: {
                listOfHonor.removeHonor(3);
                break;
            }
            case R.id.iv_remove5: {
                listOfHonor.removeHonor(4);
                break;
            }
        }
        if (numOfVisLayout > 1) {
            numOfVisLayout--;
            for (int i = 4; i > numOfVisLayout - 1; i--) {
                honorLayoutList.get(i).setVisibility(View.GONE);
            }
        }
        mLyAddExperience.setEnabled(true);
        for (int i = 0; i < 5; i++) {
            removeIvList.get(i).setVisibility(View.GONE);
        }
        if (numOfVisLayout == 1) mLyRemoveExperience.setEnabled(false);
        refreshLayout();
    }

    /**
     * 页面底部的按钮 删除教学经历 添加教学经历
     * Buttons in the bottom of the page
     * Remove experience
     * Add experience
     */
    @OnClick({R.id.layout_add_experience, R.id.layout_remove_experience})
    void doBottomButton(View view) {
        switch (view.getId()) {
            case R.id.layout_add_experience: {
                if (numOfVisLayout < 5) {
                    numOfVisLayout++;
                    for (int i = 0; i < numOfVisLayout; i++) {
                        honorLayoutList.get(i).setVisibility(View.VISIBLE);
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

    /**
     * 根据数据刷新布局
     */
    void refreshLayout() {
        for (int i = 0; i < 5; i++) {
            if (listOfHonor.get(i).getNote().isEmpty()) {
                writedTvList.get(i).setText("未填写");
                writedTvList.get(i).setTextColor(Color.rgb(48, 48, 48));
            } else {
                writedTvList.get(i).setText("已填写");
                writedTvList.get(i).setTextColor(Color.rgb(0, 153, 255));
            }
            if (i < numOfVisLayout) honorLayoutList.get(i).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 存储教学经历的BEAN
     * beginTime - 开始时间
     * endTime - 结束时间
     * note - 教学经历
     */
    class Honor {
        String note;
        String url;

        Honor(String note, String url) {
            this.note = note;
            this.url = url;
        }

        String getNote() {
            return note;
        }

        Honor setNote(String note) {
            this.note = note;
            return this;
        }

        String getUrl() {
            return url;
        }

        Honor setUrl(String url) {
            this.url = url;
            return this;
        }

        @NonNull
        @Override
        public String toString() {
            return "荣誉: " + note + "证书: " + url + "\n";
        }
    }

    /**
     * 操作教学经历beans的对象
     */
    class ListOfHonor {

        List<Honor> honorList = new ArrayList<>();
        int num = 5;

        ListOfHonor() {
            for (int i = 0; i < num; i++) {
                honorList.add(new Honor("", ""));
            }
        }

        void addHonor(int index, String note, String url) {
            honorList.get(index).setNote(note).setUrl(url);
        }

        void removeHonor(int index) {
            honorList.remove(index);
            honorList.add(new Honor("", ""));
        }

        public Honor get(int index) {
            return honorList.get(index);
        }

        @NonNull
        @Override
        public String toString() {
            return honorList.get(0).toString() + honorList.get(1).toString() + honorList.get(2).toString()
                    + honorList.get(3).toString() + honorList.get(4).toString() + "\n" + num;
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
