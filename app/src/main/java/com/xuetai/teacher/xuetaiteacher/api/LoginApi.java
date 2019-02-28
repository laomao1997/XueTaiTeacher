package com.xuetai.teacher.xuetaiteacher.api;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.BuildConfig;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.http.ApiInterface;
import com.xuetai.teacher.xuetaiteacher.models.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginApi {

    ApiInterface apiInterface;

    // 构建私有构造方法
    private LoginApi() {

        // 创建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) // 设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 设置 RxJava 适配器
                .client(genericClient())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

    }

    // 创建单例
    private static class SingleHolder{
        private static final LoginApi instance = new LoginApi();
    }

    // 修改请求头
    private OkHttpClient genericClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(CachingControlInterceptor.REWRITE_RESPONSE_INTERCEPTOR)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    // 获取单例
    public static LoginApi getInstance() {
        return SingleHolder.instance;
    }

    public void getLoginResult(@NonNull String phone, @NonNull String password, Subscriber<ResponseBody> subscriber) {

        // 拼接 the request JSON
        JSONObject jsonparams = getParamsToJson(phone, password);
        JSONObject jsonObject = new JSONObject(); // 最外层的 JSONObeject 对象
        try {
            jsonObject.put("params", jsonparams);
            jsonObject.put("method", MethodCode.Login);
            jsonObject.put("id", 1);
        } catch (Exception e) {
            KLog.e(e);
        }

        apiInterface.getCall(jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 将 电话号码 和 密码 拼接为JSON
     *
     * @param mobile   手机号
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

}
