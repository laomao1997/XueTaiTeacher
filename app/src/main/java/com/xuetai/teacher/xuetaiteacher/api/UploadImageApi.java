package com.xuetai.teacher.xuetaiteacher.api;

import com.xuetai.teacher.xuetaiteacher.BuildConfig;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.http.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UploadImageApi {

    ApiInterface apiInterface;

    private UploadImageApi() {
        // 创建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) // 设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 设置 RxJava 适配器
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    // 创建单例
    private static class SingleHolder {
        private static final UploadImageApi instance = new UploadImageApi();
    }

    public static UploadImageApi getInstance() {
        return SingleHolder.instance;
    }

    public void uploadImageResult(String base64, Subscriber<ResponseBody> subscriber) {
        JSONObject jsonObject = new JSONObject(); // 最外层的 JSONObeject 对象
        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("content", base64);
            jsonParams.put("extension", "png");
            jsonParams.put("sub", "teacher_info");
            jsonObject.put("params", jsonParams);
            jsonObject.put("method", MethodCode.UPLOADIMAGE);
            jsonObject.put("id", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        apiInterface.getCall(jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
