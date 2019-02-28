package com.xuetai.teacher.xuetaiteacher.api;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.BuildConfig;
import com.xuetai.teacher.xuetaiteacher.http.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取教师课程列表 接口
 * Created by Jinghao Zhang on 2018/11/30
 */
public class CourseQueryListApi {

    private String courseResult = "";

    private ArrayList<String> courseList = new ArrayList<>();

    ApiInterface apiInterface;


    private CourseQueryListApi() {

        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) // 设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 设置 RxJava 适配器
                .client(genericClient())
                .build();


        apiInterface = retrofit.create(ApiInterface.class);

    }

    // 创建单例
    private static class SingleHolder {
        private static final CourseQueryListApi instance = new CourseQueryListApi();
    }

    // 获取单例
    public static CourseQueryListApi getInstance() {
        return SingleHolder.instance;
    }

    // 修改请求头为登陆成功后的SID
    private OkHttpClient genericClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(CachingControlInterceptor.REWRITE_RESPONSE_INTERCEPTOR)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取课程结果
     * @param type 课程类型 0-待上的课 1-已上的课
     * @param subscriber 需要传入的订阅者
     */
    public void getCourseResult(int type, Subscriber<ResponseBody> subscriber) {

        String loginArgs = "{\"method\":\"Teacher/Course.queryList\",\"id\":1,\"params\":{\"type\":" + type + ",\"num\":20,\"offset\":0}}";

        apiInterface.getCall(loginArgs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

}
