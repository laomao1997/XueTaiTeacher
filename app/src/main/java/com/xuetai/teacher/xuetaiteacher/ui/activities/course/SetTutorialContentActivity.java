package com.xuetai.teacher.xuetaiteacher.ui.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.TextbookAdapter;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.models.course.TextBook;

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
 * 设置辅导内容
 * Author: Jinghao Zhang
 * Email: zhang.jing.hao@outlook.com
 * Date: 2019/1/25
 */
public class SetTutorialContentActivity extends AppCompatActivity {

    String id = "";

    String departTitle = ""; // 总复习
    String departContent = ""; // 总复习内容
    String textbookTitle = ""; // 教科书
    List<TextBook> textbooks = new ArrayList<>(); // 教科书列表

    BaseAdapter adapter; // 教科书列表adapter

    @BindView(R.id.iv_back_arrow)
    ImageView ivBackBtn;
    @BindView(R.id.tv_depart_title)
    TextView tvDepartTitle;
    @BindView(R.id.tv_depart_content)
    TextView tvDepartContent;
    @BindView(R.id.tv_textbook_title)
    TextView tvTextbookTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tutorial_content);

        ButterKnife.bind(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
//        KLog.debug("设置课程内容获得ID", id);
        ivBackBtn.setOnClickListener(view -> finish());
        getData();
    }

    void initView() {
        TextbookAdapter adapter = new TextbookAdapter(textbooks);
        adapter.setOnItemClickListener(new TextbookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                jumpToTextbook(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
                //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
                return false;
            }
        });
        //解决数据加载不完的问题
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        //解决数据加载完成后, 没有停留在顶部的问题
        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);
    }

    void getData() {
        JSONObject jsonParams = new JSONObject();
        NormalApi.getInstance().getResult(MethodCode.TextBookVersionList, jsonParams, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    JSONArray jsonArray = new JSONObject(responseBody.string()).getJSONArray("result");
//                    KLog.json("设置辅导内容", jsonArray.toString());
                    departTitle = jsonArray.getJSONObject(0).getString("title");
                    departContent = jsonArray.getJSONObject(0).getJSONArray("content").getString(0);
                    textbookTitle = jsonArray.getJSONObject(1).getString("title");
                    loadTextbookList(jsonArray.getJSONObject(1).getJSONArray("content"));
//                    KLog.a("title " + textbookTitle);
                    refreshView();
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    void refreshView() {
        tvDepartTitle.setText(departTitle);
        tvDepartContent.setText(departContent);
        tvTextbookTitle.setText(textbookTitle);
    }

    // 解析获得的教科书jsonArray
    void loadTextbookList(JSONArray jsonArray) {
//        KLog.json(jsonArray.toString());
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                textbooks.add(
                        new TextBook(
                                jsonArray.getJSONObject(i).getInt("id"),
                                jsonArray.getJSONObject(i).getString("name"))
                );
//                KLog.d("加载教科书", textbooks.get(i).getName());
            }
        } catch (Exception e) {
            KLog.e(e);
        }

        initView();
    }

    // 跳转到对应教科书的年级选择页面
    void jumpToTextbook(int postion) {
        Intent intent = new Intent(SetTutorialContentActivity.this, SetGradeActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("NAME", textbooks.get(postion).getName());
        startActivity(intent);
    }

    @OnClick(R.id.tv_depart_content)
    void doSetDepart() {
        Intent intent = new Intent(SetTutorialContentActivity.this, SetTutorialTriMultiListActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("VERSION", departTitle);
        intent.putExtra("GRADE", "");
        startActivity(intent);
    }

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
