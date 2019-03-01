package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class CourseSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_setting);

        // 设置沉浸式状态栏
        // 当 FitsSystemWindows 设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        // 设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        // 一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 会导致状态栏文字看不清
        // 所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            // 如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            // 这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

        ButterKnife.bind(this);

        getPriceFromRemote();
    }

    /**
     * 从服务端获取1对1价格
     */
    void getPriceFromRemote() {
        NormalApi.getInstance().getResult(MethodCode.IncomeDetail, new JSONObject(), new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String price = new JSONObject(responseBody.string()).toString();
                    KLog.json(price);
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    /**
     * 将1对1价格设置到TextView
     * @param s 1对1价格
     */
    void setPriceToView(String s) {
        SuperTextView tvPrice = (SuperTextView) findViewById(R.id.tv_price);
        tvPrice.setRightString(s);
    }

    @OnClick({R.id.tv_price, R.id.tv_schedule_time, R.id.tv_series_1vs1,
    R.id.tv_tutorial, R.id.tv_1vs1})
    void doDealWithThem(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_price:
                break;
            case R.id.tv_schedule_time:
                intent = new Intent(CourseSettingActivity.this, ScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_series_1vs1:
                toastMessage("系列课开课时间还没有开始");
                break;
            case R.id.tv_tutorial:
                toastMessage("你没有专题课开课权限");
                break;
            case R.id.tv_1vs1:
                toastMessage("你没有同步班开课权限");
                break;
        }
    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }

    /**
     * 显示一个短时间的提示信息
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
