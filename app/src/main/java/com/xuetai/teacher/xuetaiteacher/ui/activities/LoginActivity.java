package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.BuildConfig;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.CachingControlInterceptor;
import com.xuetai.teacher.xuetaiteacher.api.LoginApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.http.ApiInterface;
import com.xuetai.teacher.xuetaiteacher.models.Result;
import com.xuetai.teacher.xuetaiteacher.models.TeacherInfo;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 登陆界面
 * Created by Jinghao Zhang on 2018/11/19
 */

public class LoginActivity extends AppCompatActivity {

    String userAgent;

    TeacherInfo teacherInfo;

    @BindView(R.id.phoneET)
    EditText phoneET;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.btn_login)
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        // 测试用账号 - 开发环境
        phoneET.setText("13333333333");
        passwordET.setText("test1234");

    }

    @OnTextChanged(R.id.phoneET)
    public void isEditTextBoxEmppty() {
        if(!(phoneET.getText().toString().equals("")
                || passwordET.getText().toString().equals("")))
        {
            if((passwordET.length() > 5) && (phoneET.length() == 13)) loginButton.setEnabled(true);
            else loginButton.setEnabled(false);
        }
        else
            loginButton.setEnabled(false);
    }

    @OnTextChanged(R.id.passwordET)
    public void isEditTextBoxEmpty() {
        if(!(phoneET.getText().toString().equals("")
                || passwordET.getText().toString().equals("")))
        {
            if((passwordET.length() > 5) && (phoneET.length() == 13)) loginButton.setEnabled(true);
            else loginButton.setEnabled(false);
        }
        else
            loginButton.setEnabled(false);
    }

    @OnClick(R.id.btn_login)
    public void doLogin() {

        String phone = phoneET.getText().toString().replace(" ", "");
        String password = passwordET.getText().toString();

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
                    String jsonstring = responseBody.string();
                    Result mResult = new Gson().fromJson(jsonstring, Result.class);
                    KLog.json(jsonstring);
                    if(mResult.getCode() == 0) {
                        // 获取登陆成功后的SID
                        JSONObject resultJson = new JSONObject(jsonstring);
                        String result = resultJson.getString("result");
                        JSONObject sidJson = new JSONObject(result);
                        String sid = sidJson.getString("sid");
                        JSONObject infoJSON = sidJson.getJSONObject("info");
                        String examineStatus = infoJSON.getString("examine_status");

                        teacherInfo = getUserInfoFromJSON(infoJSON);

                        // 将SID重写进请求头
                        CachingControlInterceptor cachingControlInterceptor = new CachingControlInterceptor();
                        cachingControlInterceptor.setUserToken(sid);

                        // 登陆持久化 - 保存SID和教师信息至SharedPreferences
                        SharedPreferences userPreference = getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = userPreference.edit();
                        editor.putString("sid", sid);
                        editor.putString("logged", examineStatus);
                        editor.putString("info", infoJSON.toString());
                        editor.putString("phone", phone);
                        editor.putString("password", password);
                        editor.apply();

                        // 页面跳转
                        Intent jumpintent;
                        if("0".equals(examineStatus)) {
                            jumpintent = new Intent(LoginActivity.this, PersonalActivity.class);
                        }
                        else {
                            // 登陆成功 跳转到主页面
                            jumpintent = new Intent(LoginActivity.this, MainActivity.class);
                        }

                        startActivity(jumpintent);
                        finish();
                    }
                    // 登陆失败
                    else toastMessage(mResult.getError().getMessage()); // 显示登陆失败的错误信息
                } catch (Exception e) {
                    toastMessage(e.getMessage());
                }
            }
        });

    }

    // 返回按钮
    @OnClick(R.id.image_back)
    public void doBack() {
        finish();
    }

    // 跳转至注册页面
    @OnClick(R.id.tv_register)
    public void doRegister() {
        Intent intentReg = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intentReg);
    }

    // 跳转至找回密码页面
    @OnClick(R.id.tv_modify_password)
    public void doModifyPassword() {
        Intent intentModifyPass = new Intent(LoginActivity.this, ModifyPasswordActivity.class);
        startActivity(intentModifyPass);
    }

    /**
     * 将 电话号码 和 密码 拼接为JSON
     * @param mobile 手机号
     * @param password 密码
     * @return
     */
    private static JSONObject getParamsToJson(String mobile, String password) {

        JSONObject json = new JSONObject();
        try {
            json.put("phone", mobile);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 解析登陆结果中的教师信息并存储在一个TeacherInfo对象中
     * @return 存储了教师所有信息的TeacherInfo对象
     */
    private TeacherInfo getUserInfoFromJSON(JSONObject jsonObject) {
        TeacherInfo teacherInfo = new TeacherInfo();
        teacherInfo.fromJsonObject(jsonObject);
        return teacherInfo;
    }

    /**
     * 显示一个短时间的提示信息
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
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
