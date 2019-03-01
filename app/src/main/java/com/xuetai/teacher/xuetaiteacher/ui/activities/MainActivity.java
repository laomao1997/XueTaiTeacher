package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.CachingControlInterceptor;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.MessageFragment;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.MyFragment;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.TimetableFragment;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {


    private Class<?> mFragmentArray[] = {TimetableFragment.class, MessageFragment.class, MyFragment.class};

    private List<Fragment> mFragments = new ArrayList<>();

    String messageJsonArgs = "";


    private int[] mImageViewArray = {R.drawable.selector_tab_timetable,
            R.drawable.selector_tab_message, R.drawable.selector_tab_my};
    private String[] mTextViewArray = {"课表", "消息", "我的"};

    MagicIndicator magicIndicator;
    CommonNavigator commonNavigator;
    ViewPager pager;
    private int numberOfUnreadMessages = 0;

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
        initNovaTabView();
//        loadDialogues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDialogues();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
     * 设置带小红点的选项卡界面
     */
    private void initNovaTabView() {
        mFragments.add(new TimetableFragment());
        mFragments.add(new MessageFragment());
        mFragments.add(new MyFragment());
        pager = findViewById(R.id.view_pager);
        pager.setAdapter(new TestAdapter(getSupportFragmentManager(), mFragments));
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        commonNavigator = new CommonNavigator(this);
    }

    private void refreshNovaTabView() {
        // 设置底部导航调整宽度和间距实现均分
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int i) {

                final BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);

                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);

                // 加载自定义选项卡view
                View tabView = LayoutInflater.from(context).inflate(R.layout.item_tab, null);
                final ImageView imageview = tabView.findViewById(R.id.tab_iv);
                imageview.setImageResource(mImageViewArray[i]);
                final TextView textview = tabView.findViewById(R.id.tab_tv);
                textview.setText(mTextViewArray[i]);
                commonPagerTitleView.setContentView(tabView);

                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
                    @Override
                    public void onSelected(int index, int totalCount) {
                        textview.setTextColor(Color.parseColor("#0099FF"));
                        imageview.setSelected(true);
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        textview.setTextColor(Color.parseColor("#BFBFBF"));
                        imageview.setSelected(false);
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        imageview.setScaleX(0.8f + (1.0f - 0.8f) * leavePercent);
                        imageview.setScaleY(0.8f + (1.0f - 0.8f) * leavePercent);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        imageview.setScaleX(0.8f + (1.0f - 0.8f) * enterPercent);
                        imageview.setScaleY(0.8f + (1.0f - 0.8f) * enterPercent);
                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pager.setCurrentItem(i);
//                        badgePagerTitleView.setBadgeView(null); // 消除tab上的小红点
                    }
                });

                badgePagerTitleView.setInnerPagerTitleView(commonPagerTitleView);

                // 初始化小红点
                if (i == 1 && numberOfUnreadMessages != 0) {
                    TextView badgeTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.simple_red_dot_badge_layout, null);
                    badgeTextView.setText("" + numberOfUnreadMessages);
                    badgePagerTitleView.setBadgeView(badgeTextView);
                } else {
                    badgePagerTitleView.setBadgeView(null);
                }

                // 设置小红点相对位置
                badgePagerTitleView.setXBadgeRule(new BadgeRule(BadgeAnchor.CENTER_X, UIUtil.dip2px(context, 5)));
                badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, UIUtil.dip2px(context, 3)));
                // 选中tab时不自动取消小红点
                badgePagerTitleView.setAutoCancelBadge(false);

                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        magicIndicator.setNavigator(commonNavigator);

        ViewPagerHelper.bind(magicIndicator, pager);
    }

    private void loadDialogues() {
        numberOfUnreadMessages = 0;
        NormalApi.getInstance().getResult(MethodCode.GetDialogues, new JSONObject(), new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    messageJsonArgs = responseBody.string();
                    JSONArray jsonArray = JSON.parseObject(messageJsonArgs).getJSONArray("result");
                    KLog.json(jsonArray.toJSONString());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                        numberOfUnreadMessages += Integer.parseInt(jsonObject.getString("unread"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(numberOfUnreadMessages);
                refreshNovaTabView();

                SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext(), "setting");
                sharedPreferencesHelper.put("MESSAGES", messageJsonArgs);
            }
        });
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

    class TestAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragment;

        public TestAdapter(FragmentManager fm, List<Fragment> mFragment) {
            super(fm);
            this.mFragment = mFragment;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragment.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return mFragment.size();
        }

    }
}
