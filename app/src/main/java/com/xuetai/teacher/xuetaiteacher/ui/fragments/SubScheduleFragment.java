package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.models.CourseBean;
import com.xuetai.teacher.xuetaiteacher.models.WeekBean;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xuetai.teacher.xuetaiteacher.constant.Constant.STATUS_EMPTY;
import static com.xuetai.teacher.xuetaiteacher.constant.Constant.STATUS_FREE;
import static com.xuetai.teacher.xuetaiteacher.constant.Constant.STATUS_NO;
import static com.xuetai.teacher.xuetaiteacher.constant.Constant.STATUS_SOLD;

public class SubScheduleFragment extends Fragment {

    private static final String TAG = "SubScheduleFragment";

    @BindView(R.id.tv_month)
    TextView tvMonth;

    int[] idOfLyTimes = {R.id.ly_7, R.id.ly_8, R.id.ly_9, R.id.ly_10, R.id.ly_11, R.id.ly_12,
            R.id.ly_13, R.id.ly_14, R.id.ly_15, R.id.ly_16, R.id.ly_17, R.id.ly_18, R.id.ly_19,
            R.id.ly_20, R.id.ly_21, R.id.ly_22, R.id.ly_23};

    @BindViews({R.id.tv_week1, R.id.tv_week2, R.id.tv_week3, R.id.tv_week4, R.id.tv_week5,
            R.id.tv_week6, R.id.tv_week7})
    List<TextView> tvWeek;

    @BindViews({R.id.tv_day1, R.id.tv_day2, R.id.tv_day3, R.id.tv_day4, R.id.tv_day5, R.id.tv_day6,
            R.id.tv_day7})
    List<TextView> tvDay;

    @BindViews({R.id.tv71, R.id.tv81, R.id.tv91, R.id.tv101, R.id.tv111, R.id.tv121, R.id.tv131,
            R.id.tv141, R.id.tv151, R.id.tv161, R.id.tv171, R.id.tv181, R.id.tv191, R.id.tv201,
            R.id.tv211, R.id.tv221, R.id.tv231})
    List<TextView> tvListDay1;

    @BindViews({R.id.tv72, R.id.tv82, R.id.tv92, R.id.tv102, R.id.tv112, R.id.tv122, R.id.tv132,
            R.id.tv142, R.id.tv152, R.id.tv162, R.id.tv172, R.id.tv182, R.id.tv192, R.id.tv202,
            R.id.tv212, R.id.tv222, R.id.tv232})
    List<TextView> tvListDay2;

    @BindViews({R.id.tv73, R.id.tv83, R.id.tv93, R.id.tv103, R.id.tv113, R.id.tv123, R.id.tv133,
            R.id.tv143, R.id.tv153, R.id.tv163, R.id.tv173, R.id.tv183, R.id.tv193, R.id.tv203,
            R.id.tv213, R.id.tv223, R.id.tv233})
    List<TextView> tvListDay3;

    @BindViews({R.id.tv74, R.id.tv84, R.id.tv94, R.id.tv104, R.id.tv114, R.id.tv124, R.id.tv134,
            R.id.tv144, R.id.tv154, R.id.tv164, R.id.tv174, R.id.tv184, R.id.tv194, R.id.tv204,
            R.id.tv214, R.id.tv224, R.id.tv234})
    List<TextView> tvListDay4;

    @BindViews({R.id.tv75, R.id.tv85, R.id.tv95, R.id.tv105, R.id.tv115, R.id.tv125, R.id.tv135,
            R.id.tv145, R.id.tv155, R.id.tv165, R.id.tv175, R.id.tv185, R.id.tv195, R.id.tv205,
            R.id.tv215, R.id.tv225, R.id.tv235})
    List<TextView> tvListDay5;

    @BindViews({R.id.tv76, R.id.tv86, R.id.tv96, R.id.tv106, R.id.tv116, R.id.tv126, R.id.tv136,
            R.id.tv146, R.id.tv156, R.id.tv166, R.id.tv176, R.id.tv186, R.id.tv196, R.id.tv206,
            R.id.tv216, R.id.tv226, R.id.tv236})
    List<TextView> tvListDay6;

    @BindViews({R.id.tv77, R.id.tv87, R.id.tv97, R.id.tv107, R.id.tv117, R.id.tv127, R.id.tv137,
            R.id.tv147, R.id.tv157, R.id.tv167, R.id.tv177, R.id.tv187, R.id.tv197, R.id.tv207,
            R.id.tv217, R.id.tv227, R.id.tv237})
    List<TextView> tvListDay7;

    int weekNum = 0;
    WeekBean weekBean = new WeekBean();

    ArrayList<String> daysList = new ArrayList<>(); // 存储了这七天的日期
    ArrayList<String> hourList = new ArrayList<>(); // 存储了每节课的时间

    ArrayList<List<TextView>> listOfTvDayLists = new ArrayList<>();

    public static SubScheduleFragment newInstance(int num) {
        SubScheduleFragment subScheduleFragment = new SubScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("WEEK_NUM", num);
        subScheduleFragment.setArguments(bundle);
//        Log.d(TAG, "newInstance: Fragment:");
        return subScheduleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_sub_schdule, container, false);

        ButterKnife.bind(this, rootView);

        init();
//        refreshView();
//        Log.d(TAG, "onCreateView: refreshView");
        bindClickListeners();
        return rootView;
    }

    /**
     * 为每个View设置点击监听
     */
    void bindClickListeners() {
        // 每个小格子的点击
        listOfTvDayLists.add(tvListDay1);
        listOfTvDayLists.add(tvListDay2);
        listOfTvDayLists.add(tvListDay3);
        listOfTvDayLists.add(tvListDay4);
        listOfTvDayLists.add(tvListDay5);
        listOfTvDayLists.add(tvListDay6);
        listOfTvDayLists.add(tvListDay7);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 17; j++) {
//                TextView textView = listOfTvDayLists.get(i).get(j);
                final int ii = i;
                final int jj = j;
                listOfTvDayLists.get(i).get(j).setOnClickListener(v -> {
                    changeSelection(ii, jj);
//                    KLog.a(generateCourseList());
                });
            }
        }
    }

    void init() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            String[] arrDayOfWeek = {"日", "一", "二", "三", "四", "五", "六"};
            Bundle bundle = getArguments();
            weekNum = bundle.getInt("WEEK_NUM");

            // 设置已过时的TextView为Disable
            if (weekNum == 0) {
                for (int i = 0; i < calendar.get(Calendar.HOUR_OF_DAY) - 5; i++) {
                    setTextViewDisable(tvListDay1.get(i));
                    tvListDay1.get(i).setEnabled(false);
                    weekBean.setCourseDiabled(0, i);
                }
            }
            calendar.add(Calendar.DAY_OF_YEAR, 7 * weekNum);
            tvMonth.setText((calendar.get(Calendar.MONTH) + 1) + "月");
            for (int i = 0; i < 7; i++) {
                daysList.add(simpleDateFormat.format(calendar.getTime()));
                tvWeek.get(i).setText(arrDayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
                tvDay.get(i).setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }


        } catch (Exception e) {
            KLog.e(e);
        }
    }

    /**
     * 从Activity获取本周日程
     * @param courseBeanArrayList
     */
    public void fillInBlanks(ArrayList<CourseBean> courseBeanArrayList) {

        hourList.add("07:00");
        hourList.add("08:00");
        hourList.add("09:00");
        hourList.add("10:00");
        hourList.add("11:00");
        hourList.add("12:00");
        hourList.add("13:00");
        hourList.add("14:00");
        hourList.add("15:00");
        hourList.add("16:00");
        hourList.add("17:00");
        hourList.add("18:00");
        hourList.add("19:00");
        hourList.add("20:00");
        hourList.add("21:00");
        hourList.add("22:00");
        hourList.add("23:00");
        for (CourseBean courseBean : courseBeanArrayList) {
//
            int day = daysList.indexOf(courseBean.getDate());
            int hour = hourList.indexOf(courseBean.getBeginTime());
            if (!courseBean.isSoldStatus()) {
                setTextViewOpen(listOfTvDayLists.get(day).get(hour));
                weekBean.setCourseFree(day, hour);
            }
            else {
                setTextViewReserved(listOfTvDayLists.get(day).get(hour), courseBean.getText());
                weekBean.setCourseSold(day, hour);
            }
        }
//        KLog.a(weekBean);
    }

    void changeSelection(int day, int hour) {
        if (weekBean.getCourse(day, hour) != 2) {
            TextView textView = listOfTvDayLists.get(day).get(hour);
            if (textView.getText().equals("OPEN")) {
                setTextViewEmpty(textView);
                weekBean.setCourseEmpty(day, hour);
            } else {
                setTextViewOpen(textView);
                weekBean.setCourseFree(day, hour);
            }
        }
//        KLog.a(weekBean);
    }

    void setTextViewDisable(TextView textView) {
        textView.setText("");
        textView.setBackgroundResource(R.drawable.schdu_bg_gray);
    }

    void setTextViewOpen(TextView textView) {
        textView.setText("OPEN");
        textView.setTextColor(Color.rgb(143, 195, 31));
        textView.setBackgroundResource(R.drawable.schdu_bg_green);
    }

    void setTextViewReserved(TextView textView, String text) {
        textView.setText(text);
        textView.setTextColor(Color.rgb(255, 255, 255));
        textView.setBackgroundResource(R.drawable.schdu_bg_blue);
    }

    void setTextViewEmpty(TextView textView) {
        textView.setText("");
        textView.setBackgroundResource(R.drawable.schdu_bg_white);
    }

    @OnClick({R.id.ly_7, R.id.ly_8, R.id.ly_9, R.id.ly_10, R.id.ly_11, R.id.ly_12,
            R.id.ly_13, R.id.ly_14, R.id.ly_15, R.id.ly_16, R.id.ly_17, R.id.ly_18, R.id.ly_19,
            R.id.ly_20, R.id.ly_21, R.id.ly_22, R.id.ly_23})
    void doDealWithTimeLy(View view) {
        int r = 0;
        switch (view.getId()) {
            case R.id.ly_7:
                r = 0;
                break;
            case R.id.ly_8:
                r = 1;
                break;
            case R.id.ly_9:
                r = 2;
                break;
            case R.id.ly_10:
                r = 3;
                break;
            case R.id.ly_11:
                r = 4;
                break;
            case R.id.ly_12:
                r = 5;
                break;
            case R.id.ly_13:
                r = 6;
                break;
            case R.id.ly_14:
                r = 7;
                break;
            case R.id.ly_15:
                r = 8;
                break;
            case R.id.ly_16:
                r = 9;
                break;
            case R.id.ly_17:
                r = 10;
                break;
            case R.id.ly_18:
                r = 11;
                break;
            case R.id.ly_19:
                r = 12;
                break;
            case R.id.ly_20:
                r = 13;
                break;
            case R.id.ly_21:
                r = 14;
                break;
            case R.id.ly_22:
                r = 15;
                break;
            case R.id.ly_23:
                r = 16;
                break;
        }
        changeRow(r);
    }

    @OnClick({R.id.ly_w0, R.id.ly_w1, R.id.ly_w2, R.id.ly_w3, R.id.ly_w4, R.id.ly_w5, R.id.ly_w6})
    void doDealWithWeekDayLy(View view) {
        int c = 0;
        switch (view.getId()) {
            case R.id.ly_w0:
                c = 0;
                break;
            case R.id.ly_w1:
                c = 1;
                break;
            case R.id.ly_w2:
                c = 2;
                break;
            case R.id.ly_w3:
                c = 3;
                break;
            case R.id.ly_w4:
                c = 4;
                break;
            case R.id.ly_w5:
                c = 5;
                break;
            case R.id.ly_w6:
                c = 6;
                break;
        }
        changeCol(c);
    }

    /**
     * 改变一行的选定状态
     */
    void changeRow(int r) {
        for (int i = 0; i < 7; i++) {
            TextView textView = listOfTvDayLists.get(i).get(r);
            if (textView.isEnabled() && textView.getText().equals(""))
                setTextViewOpen(textView);
        }
        weekBean.setHourFree(r);
    }

    /**
     * 改变一列的选定状态
     */
    void changeCol(int c) {
        for (TextView textView : listOfTvDayLists.get(c)) {
            if (textView.isEnabled() && textView.getText().equals("")) {
                setTextViewOpen(textView);
            }
        }
        weekBean.setDayFree(c);
    }


    public ArrayList<String> generateCourseList() {

        ArrayList<String> arrayList = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 17; hour++) {
                if (weekBean.getCourse(day, hour) == STATUS_FREE) {
                    arrayList.add(daysList.get(day) + " " + hourList.get(hour));
                }
            }
        }
        return arrayList;
    }

    public int[][] getWeekBeenSchedule() {
        return weekBean.getSchedule();
    }

    public void addFromPreviousWeek(int[][] preSchedule) {
        for (int day = 0; day < 7; day ++) {
            for (int hour = 0; hour < 17; hour ++) {
                if (preSchedule[day][hour] == 1 && weekBean.getCourse(day, hour) == 0) {
                    weekBean.setCourseFree(day, hour);
                    setTextViewOpen(listOfTvDayLists.get(day).get(hour));
                }
            }
        }
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
