package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.MyClassAdapter;
import com.xuetai.teacher.xuetaiteacher.api.CourseQueryListApi;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.ui.activities.SetLessonActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class UnfinishedSubFragment extends Fragment {

    private static final String TAG = "UnfinishedSubFragment";

    ArrayList courseList = new ArrayList();

    MyClassAdapter myClassAdapter;

    List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

    @BindView(R.id.main_lv)
    ListView listView;

    @BindView(R.id.main_srl)
    SwipeRefreshLayout mSwipeLayout;

    @BindView(R.id.empty_view)
    LinearLayout mEmptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.subfragment_unfinished_timetable, null);
        ButterKnife.bind(this, v);

        SharedPreferences userPreference = this.getActivity().getSharedPreferences("setting", 0);
        String realsid = userPreference.getString("sid", "default");
//        System.out.println("通过SharedPreferences获得的SID为：" + realsid);

        initSwipeRefreshLayout();

        getCoursesFromRemote();

        myClassAdapter = new MyClassAdapter(datas, this.getContext());
        listView.setAdapter(myClassAdapter);
        listView.setDividerHeight(0);

        listView.setEmptyView(mEmptyView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                initialSetCourse(datas.get(position).get("id").toString());
            } catch (Exception e) {
                toastMessage(e.getMessage());
            }
        });

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            //刷新数据
            getCoursesFromRemote();
        }
        else {
            //相当于Fragment的onPause
        }
    }

    /**
     * 打开相应课程设置页面
     */
    private void initialSetCourse(String s) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", s);
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.CourseDetailInitial, jsonParams, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string()).getJSONObject("result");
                    if (jsonObject.getString("type").equals("1")) {
                        Intent intent = new Intent(getContext(), SetLessonActivity.class);
                        intent.putExtra("LESSON_DETAIL", jsonObject.toString());
                        startActivity(intent);
                    }
                    else {
                        KLog.json(jsonObject.toString());
                        Intent intent3 = new Intent(getContext(), SetLessonActivity.class);
                        intent3.putExtra("LESSON_DETAIL", jsonObject.toString());
                        startActivity(intent3);
                    }
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    private void getCoursesFromRemote() {

        courseList.clear();

        CourseQueryListApi.getInstance().getCourseResult(0, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String courseResult = responseBody.string();
//                    KLog.json("课程结果", courseResult);
                    JSONObject courseresultJson = new JSONObject(courseResult);
                    JSONArray courseJsonArray = courseresultJson.getJSONArray("result");
                    for (int i = 0; i < courseJsonArray.length(); i++) {
                        courseList.add(courseJsonArray.get(i).toString());
                    }
                } catch (Exception e) {
                    {
                        System.out.println(e.getMessage());
                    }
                }

                setListView(courseList);
            }
        });
    }

    // 初始化列表
    private void setListView(ArrayList courseList) {
        datas.clear();
        for (int i = 0; i < courseList.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject(courseList.get(i).toString());
                // System.out.println("ID: " + jsonObject.getString("id"));
                Map map = new HashMap<>();
                map.put("actionDesc", jsonObject.getString("actionDesc"));
                map.put("id", jsonObject.getString("id"));
                map.put("studentName", jsonObject.getString("studentName"));
                map.put("beginTime", jsonObject.getString("beginTime"));
                //map.put("tagText", jsonObject.getString("tagText"));
                //map.put("tagShort", jsonObject.getString("tagShort"));
                String tag = jsonObject.getString("tag");
                map.put("tag", tag);
                if (tag.equals("1")) {
                    map.put("studentHead", jsonObject.getString("studentHead"));
                }
                datas.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mSwipeLayout.setRefreshing(false);
        myClassAdapter = new MyClassAdapter(datas, this.getContext());
        listView.setAdapter(myClassAdapter);
        changeVisibility();
    }

    // 当课程列表courseList为空时，隐藏列表，显示空页面
    private void changeVisibility() {
        if (courseList.isEmpty()) {
            mSwipeLayout.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mSwipeLayout.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    // 下拉刷新
    private void initSwipeRefreshLayout() {

        mSwipeLayout.setColorSchemeColors(Color.rgb(0, 153, 255));

        mSwipeLayout.setOnRefreshListener(() -> getCoursesFromRemote());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public UnfinishedSubFragment() {
        // 空的构造函数
    }

    public static UnfinishedSubFragment newInstance() {
        UnfinishedSubFragment unfinishedSubFragment = new UnfinishedSubFragment();
        return unfinishedSubFragment;
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