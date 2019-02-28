package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.BuildConfig;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.http.ApiInterface;
import com.xuetai.teacher.xuetaiteacher.models.Result;

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
 * 注册页面
 * Created by Jinghao Zhang on 2018/11/20
 */

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.et_phone)
    EditText editTextPhone;

    @BindView(R.id.et_verification_code)
    EditText editTextCode;

    @BindView(R.id.et_password)
    EditText editTextPassword;

    @BindView(R.id.btn_register)
    Button btnRegister;

    @BindView(R.id.btn_get_sms_code)
    Button btnGetSmsCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

    }

    /**
     * 重新设置用户的密码
     *
     * @param phone    需要重设密码的手机号
     * @param code     短信验证码 开发环境默认为2035
     * @param password 新密码
     */
    private void register(String phone, String code, String password) {
        // 拼接 the request JSON
        JSONObject jsonparams = getParamsToJson(phone, code, password);
        NormalApi.getInstance().getResult(MethodCode.Register, jsonparams, new Subscriber<ResponseBody>() {
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
                    if (mResult.getCode() == 0) {
                        toastMessage("注册成功");
                        JSONObject resultJson = new JSONObject(jsonstring);
                        String sid = resultJson.getJSONObject("result").getString("sid");
                        String examineStatus = "0";
                        // 登陆持久化 - 保存SID和教师信息至SharedPreferences
                        SharedPreferences userPreference = getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = userPreference.edit();
                        editor.putString("sid", sid);
                        editor.putString("logged", examineStatus);
                        editor.putString("phone", phone);
                        editor.putString("password", password);
                        editor.apply();
                        Intent intentReg = new Intent(RegisterActivity.this, PersonalActivity.class);
                        startActivity(intentReg);
                    } else toastMessage(mResult.getError().getMessage());
                } catch (Exception e) {
                    toastMessage(e.getMessage());
                }
            }
        });

    }

    /**
     * 通知服务器发送一条短信到指定的手机上
     *
     * @param phone 希望接收短信的手机号码
     */
    private void getSmsCode(String phone) {
        // 拼接 the request JSON
        JSONObject jsonparams = new JSONObject();
        try {
            jsonparams.put("mobile", phone);
            jsonparams.put("type", "0"); // 0 - 注册; 1 - 修改密码
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.SmsCode, jsonparams, new Subscriber<ResponseBody>() {
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
                    if (mResult.getCode() == 0) toastMessage("请填写短信验证码");
                    else toastMessage(mResult.getError().getMessage());
                } catch (Exception e) {
                    toastMessage(e.getMessage());
                }
            }
        });
    }

    @OnTextChanged(R.id.et_phone)
    public void isEditTextBoxOK() {
        if (!(editTextPhone.getText().toString().equals("")
                || editTextCode.getText().toString().equals("")
                || editTextPassword.getText().toString().equals(""))) {
            if ((editTextPassword.length() > 5) && (editTextPhone.length() == 13))
                btnRegister.setEnabled(true);
            else btnRegister.setEnabled(false);
        } else
            btnRegister.setEnabled(false);

        if (editTextPhone.length() == 13) btnGetSmsCode.setEnabled(true);
        else btnGetSmsCode.setEnabled(false);
    }

    @OnTextChanged(R.id.et_verification_code)
    public void isEditTextBoxesOk() {
        if (!(editTextPhone.getText().toString().equals("")
                || editTextCode.getText().toString().equals("")
                || editTextPassword.getText().toString().equals(""))) {
            if ((editTextPassword.length() > 5) && (editTextPhone.length() == 13))
                btnRegister.setEnabled(true);
            else btnRegister.setEnabled(false);
        } else
            btnRegister.setEnabled(false);
    }

    @OnTextChanged(R.id.et_password)
    public void isEditTextBoxsssOKK() {
        if (!(editTextPhone.getText().toString().equals("")
                || editTextCode.getText().toString().equals("")
                || editTextPassword.getText().toString().equals(""))) {
            if ((editTextPassword.length() > 5) && (editTextPhone.length() == 13))
                btnRegister.setEnabled(true);
            else btnRegister.setEnabled(false);
        } else
            btnRegister.setEnabled(false);
    }

    /**
     * 注册
     */
    @OnClick(R.id.btn_register)
    public void doRegister() {
        String phone = editTextPhone.getText().toString().replace(" ", "");
        String code = editTextCode.getText().toString();
        String password = editTextPassword.getText().toString();
        register(phone, code, password);
    }

    /**
     * 返回按钮
     */
    @OnClick(R.id.image_back)
    public void doBack() {
        doLogin();
    }

    /**
     * 跳转至登陆界面
     */
    @OnClick({R.id.tv_login})
    public void doLogin() {
        Intent intentLog = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intentLog);
        finish();
    }

    // 获取短信验证码按钮
    @OnClick(R.id.btn_get_sms_code)
    public void doGetSmsCode() {
        String mobile = editTextPhone.getText().toString().replace(" ", "");
        getSmsCode(mobile);
    }

    /**
     * 将 电话号码 和 密码 拼接为JSON
     *
     * @param mobile   手机号
     * @param password 密码
     * @return
     */
    private static JSONObject getParamsToJson(String mobile, String code, String password) {

        JSONObject json = new JSONObject();
        try {
            json.put("phone", mobile);
            json.put("code", code);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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