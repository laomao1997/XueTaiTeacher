package com.xuetai.teacher.xuetaiteacher.api;

import android.webkit.WebSettings;

import okhttp3.Interceptor;
import okhttp3.Request;

public class CachingControlInterceptor {
    private static final int TIMEOUT_DISCONNECT = 60 * 60 * 24 * 7;

    private static String userToken = "";

    static final Interceptor REWRITE_RESPONSE_INTERCEPTOR = chain -> {
        System.out.println(userToken);
        Request request = chain.request()
                .newBuilder()
                .header("SID", userToken)
                .removeHeader("User-Agent") //移除旧的
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; MI 6  Build/NMF26X) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36") //添加真正的头部
                .build();
        return chain.proceed(request);
    };

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        CachingControlInterceptor.userToken = userToken;
    }

    /*public static final Interceptor REWRITE_RESPONSE_INTERCEPTOR_OFFLINE = chain -> {
        Request request = chain.request();
        if (!NetUtils.isConnected(App.getContext())) {
            request = chain.request();
            if (!NetUtils.isConnected(App.getContext())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetUtils.isConnected(App.getContext())) {
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder().header("Cache-Control",
                        "public, only-if-cached, max-stale=" + TIMEOUT_DISCONNECT).removeHeader("Pragma").build();
            }
        }
        return chain.proceed(request);
    };


    public static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {

        CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        cacheBuilder.maxAge(0, TimeUnit.SECONDS);
        cacheBuilder.maxStale(365, TimeUnit.DAYS);
        CacheControl cacheControl = cacheBuilder.build();
        Request request = chain.request();
        if (!NetUtils.isConnected(App.getContext())) {
            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        if (NetUtils.isConnected(App.getContext())) {
            int maxAge = 0; // read from cache
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public ,max-age=" + maxAge)
                    .addHeader("Content-Type", "application/json")
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .addHeader("Content-Type", "application/json")
                    .build();
        }
    };*/
}