package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
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
                break;
            case R.id.tv_tutorial:
                break;
            case R.id.tv_1vs1:
                break;
        }
    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }
}
