package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.SchedulePageAdapter;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadFreeTimeApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.models.CourseBean;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.SubScheduleFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 18;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.ly_top)
    RelativeLayout mLyTop;

    Map<String, Integer> daysMap = new HashMap<>();
    List<Fragment> scheduleList = new ArrayList<>();
    ArrayList<ArrayList> weeks = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schdule);

        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        for (int i = 0; i < NUM_PAGES; i++) {
            scheduleList.add(SubScheduleFragment.newInstance(i));

            // 建立每个日期和对应周数的索引表
//            for (String dateString : schedule.getScheduleWeek(i).getDateList()) {
//
//                daysMap.put(dateString, i);
//            }
            // 建立每个日期和对应周数的索引表
            for (int j = 0; j < 7; j++) {
                daysMap.put(simpleDateFormat.format(calendar.getTime()), i);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }


        // The pager adapter, which provides the pages to the view pager widget.
        SchedulePageAdapter mSchedulePageAdapter = new SchedulePageAdapter(getSupportFragmentManager(), scheduleList);
        getCalender();
        viewPager.setAdapter(mSchedulePageAdapter);
        viewPager.setOffscreenPageLimit(18);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
//                ((SubScheduleFragment) (scheduleList.get(i))).fillInBlanks(weeks.get(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
//                ((SubScheduleFragment) (scheduleList.get(i))).fillInBlanks(weeks.get(i));
            }
        });
//        getCalender();

    }

    void getCalender() {
        NormalApi.getInstance().getResult(MethodCode.TeacherInfoCalendar, new org.json.JSONObject(), new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    loadData(responseBody.string());
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    void loadData(String responseBody) {
        try {
            JSONArray resultArray = JSON.parseObject(responseBody).getJSONObject("result").getJSONArray("list");
            ArrayList<CourseBean> courseBeanArrayList = JSON.parseObject(resultArray.toString(), new TypeReference<ArrayList<CourseBean>>() {});
            for (int i =0; i < 18; i++) {
                weeks.add(new ArrayList<CourseBean>());
            }

            for (CourseBean courseBean : courseBeanArrayList) {
                weeks.get(daysMap.get(courseBean.getDate())).add(courseBean);
            }

            for (int i = 0; i < 18; i++) {
                ((SubScheduleFragment) (scheduleList.get(i))).fillInBlanks(weeks.get(i));
            }
        } catch (Exception e) {
              KLog.e(e);
        }
    }

    void updateTeacherFreeTime() {
        ProgressDialog waitingDialog=
                new ProgressDialog(ScheduleActivity.this);
        waitingDialog.setTitle("时间表上传中");
        waitingDialog.setMessage("请稍后...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            arrayList.addAll(((SubScheduleFragment) (scheduleList.get(i))).generateCourseList());
        }
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(arrayList));
//        KLog.a(jsonArray.get(5));
        UploadFreeTimeApi.getInstance().uploadResult(jsonArray, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                // 成功
                waitingDialog.dismiss();
//                try {
//                    KLog.json(responseBody.string());
//                } catch (Exception e) {
//                    KLog.e(e);
//                }
                finish();

            }
        });
    }

    void copyScheduleToNextPage(int current) {
        if (current < 17) {
            ((SubScheduleFragment) scheduleList.get(current+1))
                    .addFromPreviousWeek(((SubScheduleFragment) scheduleList.get(current))
                            .getWeekBeenSchedule());
        }
    }

    @OnClick({R.id.tv_confirm, R.id.tv_bottom})
    void doDealWithThem(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                updateTeacherFreeTime();
                break;
            case R.id.tv_bottom:
                copyScheduleToNextPage(viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() != 18) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
                break;
        }
    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }
}
