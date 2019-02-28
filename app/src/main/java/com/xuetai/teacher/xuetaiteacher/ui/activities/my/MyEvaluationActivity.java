package com.xuetai.teacher.xuetaiteacher.ui.activities.my;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.EvaluationPageAdapter;
import com.xuetai.teacher.xuetaiteacher.adapters.TimetablePageAdapter;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.models.CommentBean;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * 我的评价
 */
public class MyEvaluationActivity extends AppCompatActivity {

    TimetablePageAdapter adapter;
    List<String> datas = new ArrayList<>();
    List<Fragment> fragments = new ArrayList<>();

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    String[] titles = {"全部", "好评", "中评", "差评"};
    int[] numbers = {0, 0, 0, 0};

    List<CommentBean> allCommentList = new ArrayList<>();
    List<CommentBean> goodCommentList = new ArrayList<>();
    List<CommentBean> mediumCommentList = new ArrayList<>();
    List<CommentBean> badCommentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_evaluation);

        // 设置沉浸式状态栏
        // 当 FitsSystemWindows 设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
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
        initData();
    }

    private void initData() {
        JSONObject jsonObject = new JSONObject();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, "setting");
        String info = (String) sharedPreferencesHelper.getSharedPreference("info", String.class);
        String id;
        try {
            jsonObject = new JSONObject(info);
//            id = jsonObject.getString("id");
//            jsonObject.put("teacherId", id);
        } catch (Exception e) {
            KLog.e(e.getMessage());
        }

        NormalApi.getInstance().getResult(MethodCode.GetComment, jsonObject, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    JSONObject resultJson = new JSONObject(responseBody.string()).getJSONObject("result");
                    for (int i = 1; i <= 3; i++) {
                        String one = resultJson.getJSONObject("" + i).getJSONObject("scoreCount").get("1").toString();
                        String two = resultJson.getJSONObject("" + i).getJSONObject("scoreCount").get("2").toString();
                        String three = resultJson.getJSONObject("" + i).getJSONObject("scoreCount").get("3").toString();
                        String all = resultJson.getJSONObject("" + i).getJSONObject("scoreCount").get("all").toString();
                        numbers[0] += Integer.parseInt(all);
                        numbers[1] += Integer.parseInt(three);
                        numbers[2] += Integer.parseInt(two);
                        numbers[3] += Integer.parseInt(one);
                        JSONArray items = resultJson.getJSONObject("" + i).getJSONArray("items");
                        for (int j = 0; j < items.length(); j++) {
                            CommentBean commentBean = getCommentFromJson(items.getJSONObject(j));
                            allCommentList.add(commentBean);
                            if (commentBean.getScore().equals("3"))
                                goodCommentList.add(commentBean);
                            if (commentBean.getScore().equals("2"))
                                mediumCommentList.add(commentBean);
                            if (commentBean.getScore().equals("1")) badCommentList.add(commentBean);
                        }
                    }

                    for (CommentBean commentBean : allCommentList) {
                        System.out.println(commentBean.getTime());
                    }
                    initView();
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    private void initView() {

        ArrayList<List<CommentBean>> arrayList = new ArrayList<>();
        arrayList.add(allCommentList);
        arrayList.add(goodCommentList);
        arrayList.add(mediumCommentList);
        arrayList.add(badCommentList);

        // 设置Adapter
        EvaluationPageAdapter evaluationPageAdapter =
                new EvaluationPageAdapter(getSupportFragmentManager(),
                        arrayList, numbers, this);

        // 给View Pager设置Adapter
        viewPager.setAdapter(evaluationPageAdapter);

        // 给tab layout设置view pager
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < 4; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(evaluationPageAdapter.getCustomView(i));
            if (i == 0) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_comment))
                        .setTextColor(Color.rgb(0, 153, 255));
                ((TextView) tab.getCustomView().findViewById(R.id.tv_number))
                        .setTextColor(Color.rgb(0, 153, 255));
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_comment))
                        .setTextColor(Color.rgb(0, 153, 255));
                ((TextView) tab.getCustomView().findViewById(R.id.tv_number))
                        .setTextColor(Color.rgb(0, 153, 255));
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_comment))
                        .setTextColor(Color.rgb(106, 106, 106));
                ((TextView) tab.getCustomView().findViewById(R.id.tv_number))
                        .setTextColor(Color.rgb(106, 106, 106));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private CommentBean getCommentFromJson(JSONObject jsonObject) {
        String id = "";
        String time = "";
        String studentName = "";
        String score = "";
        String comment = "";
        String reply = "";
        try {
            id = jsonObject.getString("id");
            time = jsonObject.getString("time");
            studentName = jsonObject.getString("studentName");
            score = jsonObject.getString("score");
            comment = jsonObject.getString("comment");
            reply = jsonObject.getString("reply");
        } catch (Exception e) {
            KLog.e(e);
        }
        return new CommentBean(id, time, studentName, score, comment, reply);
    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }
}
