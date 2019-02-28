package com.xuetai.teacher.xuetaiteacher.api;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.BuildConfig;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.http.ApiInterface;

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

public class UploadInfoApi {

    ApiInterface apiInterface;

    private UploadInfoApi() {
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
    private static final class SingleHolder {
        private static final UploadInfoApi intance = new UploadInfoApi();
    }

    // 获取单例
    public static UploadInfoApi getInstance() {
        return SingleHolder.intance;
    }

    // 修改请求头为登陆成功后的SID
    private OkHttpClient genericClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(CachingControlInterceptor.REWRITE_RESPONSE_INTERCEPTOR)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

//    public void uploadPersonalResult(String avatarUrl, String realName, String sex, String cityName, Subscriber<ResponseBody> subscriber) {
//        JSONObject jsonObject = new JSONObject(); // 最外层的 JSONObeject 对象
//        JSONObject jsonParams = new JSONObject();
//
//        try {
//            jsonParams.put("head_photo", avatarUrl);
//            jsonParams.put("real_name", realName);
//            jsonParams.put("gender", sex);
//            jsonParams.put("location", cityName);
//            jsonObject.put("params", jsonParams);
//            jsonObject.put("method", "Teacher/Info.update");
//            jsonObject.put("id", 1);
//        } catch (Exception e) {
//            KLog.e(e);
//        }
//
//        apiInterface.getCall(jsonObject.toString())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//    }

    public void uploadResult(JSONObject jsonParams, Subscriber<ResponseBody> subscriber) {
        JSONObject jsonObject = new JSONObject(); // 最外层的 JSONObeject 对象

        try {
            jsonObject.put("params", jsonParams);
            jsonObject.put("method", MethodCode.UpDateInfo);
            jsonObject.put("id", 1);
        } catch (Exception e) {
            KLog.e(e);
        }

        apiInterface.getCall(jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

//    public void uploadEduResult(JSONObject jsonParams, Subscriber<ResponseBody> subscriber) {
//        JSONObject jsonObject = new JSONObject(); // 最外层的 JSONObeject 对象
//
//        try {
//            jsonObject.put("params", jsonParams);
//            jsonObject.put("method", "Teacher/Info.update");
//            jsonObject.put("id", 1);
//        } catch (Exception e) {
//            KLog.e(e);
//        }
//    }

}
