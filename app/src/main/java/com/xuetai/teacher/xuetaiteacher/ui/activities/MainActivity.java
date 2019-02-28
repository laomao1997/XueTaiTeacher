package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.CachingControlInterceptor;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.MessageFragment;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.MyFragment;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.TimetableFragment;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    private Class<?> mFragmentArray[] = {TimetableFragment.class, MessageFragment.class, MyFragment.class};

    private int[] mImageViewArray = {R.drawable.selector_tab_timetable,
            R.drawable.selector_tab_message, R.drawable.selector_tab_my};
    private String[] mTextViewArray = {"课表", "消息", "我的"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置沉浸式状态栏
        // 当 FitsSystemWindows 设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
//        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
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


        checkLogin();

        initTabView();
        // initTab();
    }

    private void checkLogin() {
        SharedPreferences userPreference = getSharedPreferences("setting", 0);
        String info = userPreference.getString("logged", "No Info");
        String sid = userPreference.getString("sid", "default");
        //System.out.println("登陆信息为" + info);

        if (("No Info").equals(info)) {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        } else if (("0").equals(info)) {
            Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
            startActivity(intent);
            finish();
        } else if (("1").equals(info)) {
            Intent intent = new Intent(MainActivity.this, ReviewStatusActivity.class);
            startActivity(intent);
            finish();
        } else {
            CachingControlInterceptor cachingControlInterceptor = new CachingControlInterceptor();
            cachingControlInterceptor.setUserToken(sid);
        }

    }

    /**
     * 设置选项卡界面
     */
    private void initTabView() {
        FragmentTabHost mTabHost = findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content_container);
        int count = mTextViewArray.length;
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));

            mTabHost.addTab(tabSpec, mFragmentArray[i], null);

            mTabHost.getTabWidget().setDividerDrawable(null);

            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        mTabHost.setCurrentTab(0);
    }


    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        ImageView imageview = view.findViewById(R.id.tab_iv);
        imageview.setImageResource(mImageViewArray[index]);
        TextView textview = view.findViewById(R.id.tab_tv);
        textview.setText(mTextViewArray[index]);
        return view;
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
}
