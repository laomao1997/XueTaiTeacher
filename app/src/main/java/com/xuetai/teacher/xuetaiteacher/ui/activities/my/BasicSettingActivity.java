package com.xuetai.teacher.xuetaiteacher.ui.activities.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.CachingControlInterceptor;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.ui.activities.LoginActivity;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class BasicSettingActivity extends AppCompatActivity {

    String phone;

    @BindView(R.id.tv_account)
    SuperTextView tvAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_setting);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        phone = intent.getStringExtra("PHONE");
        setPhoneToView(tvAccount);
    }

    void setPhoneToView(SuperTextView textView) {
        textView.setRightString(phone);
    }

    void logout() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("phone", phone);
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.Logout, jsonParams, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    boolean result = new JSONObject(responseBody.string()).getBoolean("result");
                    if (result) {
                        // 将请求头中的sid去除
                        CachingControlInterceptor cachingControlInterceptor = new CachingControlInterceptor();
                        cachingControlInterceptor.setUserToken("");
                        //清楚保存在SharedPreferences中的用户信息
                        SharedPreferencesHelper helper = new SharedPreferencesHelper(getApplicationContext(), "setting");
                        helper.clear();
                        //页面跳转
                        Intent intent = new Intent(BasicSettingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
//                    KLog.json("登出!", responseBody.string());
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });

    }

    @OnClick({R.id.iv_back_arrow, R.id.tv_account, R.id.tv_modify_password,
            R.id.tv_about, R.id.btn_logout})
    void doDealWithThem(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.tv_account:
                break;
            case R.id.tv_modify_password:
                break;
            case R.id.tv_about:
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

}
