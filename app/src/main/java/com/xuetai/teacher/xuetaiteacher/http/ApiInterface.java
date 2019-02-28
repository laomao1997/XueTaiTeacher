package com.xuetai.teacher.xuetaiteacher.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface ApiInterface {

    @POST("api/index.php")
//    @POST("wtl_api/index.php")
    @FormUrlEncoded // 表示请求体是一个Form表单
    Observable<ResponseBody> getCall(@Field("request") String methodCodeArgs);

}
