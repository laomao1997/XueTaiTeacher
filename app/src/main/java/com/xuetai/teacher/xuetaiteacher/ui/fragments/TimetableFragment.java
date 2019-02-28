package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.TimetablePageAdapter;
import com.xuetai.teacher.xuetaiteacher.api.CourseQueryListApi;
import com.xuetai.teacher.xuetaiteacher.ui.activities.ScheduleActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimetableFragment extends Fragment {


    TimetablePageAdapter adapter;
    List<String> datas = new ArrayList<>();
    List<Fragment> fragments = new ArrayList<>();

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timetable, null);

        ButterKnife.bind(this, v);

        intiView();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.iv_open_schdule)
    public void doOpenSchdule() {
        Intent intent = new Intent(getContext(), ScheduleActivity.class);
        startActivity(intent);
    }


    private void intiView() {

        // 设置Adapter
        TimetablePageAdapter timetablePageAdapter = new TimetablePageAdapter(getChildFragmentManager());
        timetablePageAdapter.addFragment(UnfinishedSubFragment.newInstance(), "待上的课");
        timetablePageAdapter.addFragment(FinishedSubFragment.newInstance(), "已上的课");

        // 给View Pager设置Adapter
        viewPager.setAdapter(timetablePageAdapter);

        // 给tab layout设置view pager
        tabLayout.setupWithViewPager(viewPager);
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
