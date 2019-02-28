package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.LoginApi;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class ReviewStatusActivity extends AppCompatActivity {

    Subscription mSubscription;

    @BindView(R.id.text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_status);

        ButterKnife.bind(this);

        doRequest();
    }

    /**
     * 轮询
     * 每隔一秒向服务器查询一次
     */
    private void doRequest() {
        SharedPreferencesHelper sharedPreferencesHelper
                = new SharedPreferencesHelper(getApplicationContext(), "setting");
        mSubscription = Observable.interval(1, 1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println(aLong);
                        JSONObject jsonParams = new JSONObject();
                        NormalApi.getInstance().getResult(MethodCode.ExamineStatus, jsonParams, new Subscriber<ResponseBody>() {
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
                                    JSONObject resultJson = new JSONObject(resultArgs);

                                    switch (resultJson.getJSONObject("result").getString("examine_status")) {
                                        case "0":
                                            System.out.println("资料没完整");
                                            break;
                                        case "1":
                                            System.out.println("资料完整没有审核");
                                            sharedPreferencesHelper.put("logged", "1");
                                            break;
                                        case "2":
                                            System.out.println("资料完整审核不通过");
                                            unSubscribe();
                                            finish();
                                            break;
                                        case "3":
                                            System.out.println("资料完整审核通过");
                                            // 登陆持久化 - 保存审核状态至SharedPreferences
//                                            SharedPreferences userPreference = getSharedPreferences("setting", 0);
//                                            String phone = userPreference.getString("phone", "no phone");
//                                            String password = userPreference.getString("password", "no password");
//                                            SharedPreferences.Editor editor = userPreference.edit();
//                                            editor.putString("logged", "3");
//                                            editor.apply();
                                            String phone = (String) sharedPreferencesHelper.getSharedPreference("phone", "no phone");
                                            String password = (String) sharedPreferencesHelper.getSharedPreference("password", "no password");
                                            sharedPreferencesHelper.put("logged", "3");
                                            // 跳转到主界面
                                            LoginApi.getInstance().getLoginResult(phone, password, new Subscriber<ResponseBody>() {
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
                                                        JSONObject result = new JSONObject(resultArgs);
                                                        String sid = result.getJSONObject("result").getString("sid");
                                                        String info = result.getJSONObject("result").getJSONObject("info").toString();
                                                        // 登陆持久化 - 保存SID和教师信息至SharedPreferences
                                                        SharedPreferences userPreference = getSharedPreferences("setting", 0);
                                                        SharedPreferences.Editor editor = userPreference.edit();
                                                        editor.putString("sid", sid);
                                                        editor.putString("info", info);
                                                        editor.apply();
                                                    } catch (Exception e) {
                                                        KLog.e(e);
                                                    }
                                                }
                                            });
                                            Intent intent = new Intent(ReviewStatusActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            unSubscribe();
                                            finish();
                                            break;
                                    }
                                } catch (Exception e) {
                                    KLog.e(e);
                                }
                            }
                        });
                    }
                });
    }

    // 停止轮询
    public void unSubscribe() {
        //判断subscribe是否已经取消订阅
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    // 设置返回按钮：不应该退出程序---而是返回桌面
    // 复写onKeyDown事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
