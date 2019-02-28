package com.xuetai.teacher.xuetaiteacher.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.BuildConfig;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.http.ApiInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UploadFreeTimeApi {

    ApiInterface apiInterface;

    private UploadFreeTimeApi() {
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
        private static final UploadFreeTimeApi intance = new UploadFreeTimeApi();
    }

    // 获取单例
    public static UploadFreeTimeApi getInstance() {
        return UploadFreeTimeApi.SingleHolder.intance;
    }

    // 修改请求头为登陆成功后的SID
    private OkHttpClient genericClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(CachingControlInterceptor.REWRITE_RESPONSE_INTERCEPTOR)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public void uploadResult(JSONArray jsonParams, Subscriber<ResponseBody> subscriber) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("params", jsonParams);
            jsonObject.put("method", MethodCode.UpdateTeacherFreeTime);
            jsonObject.put("id", 1);
        } catch (Exception e) {
            KLog.e(e);
        }

        apiInterface.getCall(jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
