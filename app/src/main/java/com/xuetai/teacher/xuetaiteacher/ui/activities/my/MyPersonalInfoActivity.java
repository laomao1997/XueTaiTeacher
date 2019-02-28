package com.xuetai.teacher.xuetaiteacher.ui.activities.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.CachingControlInterceptor;
import com.xuetai.teacher.xuetaiteacher.api.LoginApi;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo.MyBasicInfoActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo.MyDiplomaIdentification;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo.MyEducationInfoActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo.MyHonorActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo.MyTeachingDemonstration;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo.MyTeachingExperiencesActivity;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class MyPersonalInfoActivity extends AppCompatActivity {

    String info;
    String password;
    String phone;

    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_personal_info);

        ButterKnife.bind(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInfo();
    }

    /**
     * 从服务器获取新的用户信息
     */
    private void updateInfo() {
        sharedPreferencesHelper = new SharedPreferencesHelper(this, "setting");
        try {
            info = sharedPreferencesHelper.getSharedPreference("info", "no_info").toString();
            password = sharedPreferencesHelper.getSharedPreference("password", "default").toString();
            phone = new JSONObject(info).getString("phone");
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
                        JSONObject jsonObject = new JSONObject(resultArgs);
                        info = jsonObject.getJSONObject("result").getJSONObject("info").toString();
                        String sid = jsonObject.getJSONObject("result").getString("sid");
                        // 将SID重写进请求头
                        CachingControlInterceptor cachingControlInterceptor = new CachingControlInterceptor();
                        cachingControlInterceptor.setUserToken(sid);
                        sharedPreferencesHelper.put("sid", sid);
                        sharedPreferencesHelper.put("info", info);
                        KLog.json("获得信息", info);
                        KLog.json("存入本地", sharedPreferencesHelper.getSharedPreference("info", "no_info").toString());

                    } catch (Exception e) {
                        KLog.e(e);
                    }
                }
            });
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    // 返回上一级页面
    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }

    // 处理各菜单项的点击事件
    @OnClick({R.id.ly_basic_info, R.id.ly_edu_info, R.id.ly_diploma_identification,
            R.id.ly_edu_exp, R.id.ly_honor_identification, R.id.ly_edu_show})
    void doOpenItem(View view) {
        switch (view.getId()) {
            //基本资料
            case R.id.ly_basic_info:
                Intent intent = new Intent(MyPersonalInfoActivity.this, MyBasicInfoActivity.class);
                startActivity(intent);
                break;
            // 教学资料
            case R.id.ly_edu_info:
                Intent intent1 = new Intent(MyPersonalInfoActivity.this, MyEducationInfoActivity.class);
                startActivity(intent1);
                break;
            // 学历认证
            case R.id.ly_diploma_identification:
                Intent intent2 = new Intent(MyPersonalInfoActivity.this, MyDiplomaIdentification.class);
                startActivity(intent2);
                break;
            // 教学经历
            case R.id.ly_edu_exp:
                Intent intent3 = new Intent(MyPersonalInfoActivity.this, MyTeachingExperiencesActivity.class);
                startActivity(intent3);
                break;
            // 荣誉认证
            case R.id.ly_honor_identification:
                Intent intent4 = new Intent(MyPersonalInfoActivity.this, MyHonorActivity.class);
                startActivity(intent4);
                break;
            // 教学展示
            case R.id.ly_edu_show:
                Intent intent5 = new Intent(MyPersonalInfoActivity.this, MyTeachingDemonstration.class);
                startActivity(intent5);
                break;
        }
    }

}
