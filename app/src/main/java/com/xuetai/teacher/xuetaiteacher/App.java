package com.xuetai.teacher.xuetaiteacher;

import android.app.Application;

import com.xuexiang.xui.XUI;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initXUI();
    }

    void initXUI() {
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
    }
}
