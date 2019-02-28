package com.xuetai.teacher.xuetaiteacher.ui.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.MulitListAdapter;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.models.TreePoint;
import com.xuetai.teacher.xuetaiteacher.ui.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Author: Jinghao Zhang
 * Email: zhang.jing.hao@outlook.com
 * Date: 2019/1/25
 */
public class SetTutorialTriMultiListActivity extends AppCompatActivity {

    int numberOfSelection = 0;

    @BindView(R.id.tv_textbook_title)
    TextView tvTextbookName;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    List<TreePoint> coursesList = new ArrayList<>();

    String id = "";
    String version = "";
    String grade = "";
    String key = "chapterBwbds";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tutorial_tri_multi_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        version = intent.getStringExtra("VERSION");
        grade = intent.getStringExtra("GRADE");

        tvTextbookName.setText("" + version + " " + grade);

        if (grade.isEmpty()) {
            getDepartData();
        } else {
            getTextbookData();
        }


    }

    // 获取总复习重点数据
    void getDepartData() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", id);
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.QuestionPointList, jsonParams, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("data");

                    getListFromJsonArray(jsonArray, true);

                } catch (Exception e) {
                    KLog.e(e);
                }

                initView();
            }
        });
    }

    private void getTextbookData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version", version);
            jsonObject.put("grade", grade);
            jsonObject.put("id", id);
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.QuestionChapterList, jsonObject, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("data");
                    KLog.json(jsonArray.toJSONString());
                    getListFromJsonArray(jsonArray, false);
                } catch (Exception e) {
                    KLog.e(e);
                }

                initView();
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        // 使用线性布局管理器
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 指定 adapter
        mAdapter = new MulitListAdapter(coursesList);
        ((MulitListAdapter) mAdapter).setOnItemClickListener(new MulitListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, TreePoint treePoint) {
                if (numberOfSelection != ((MulitListAdapter) mAdapter).getNumOfSelected()) {
                    numberOfSelection = ((MulitListAdapter) mAdapter).getNumOfSelected();
                    tvNumber.setText(String.valueOf(numberOfSelection));
                }
            }
        });

        recyclerView.setAdapter(mAdapter);
    }

    private void getListFromJsonArray(JSONArray jsonArray, boolean isDepart) {
        if (isDepart) key = "exams";
        loadTreePoint(jsonArray, 0, 1, key);
//        for (int i = 0; i < jsonArray.size(); i++) {
//            TreePoint treePoint1 = getTreePointFromJson(jsonArray.getJSONObject(i), 0, 1);
//            coursesList.add(treePoint1);
//            if (!treePoint1.isLeaf()) {
//                int pid2 = treePoint1.getId();
//                JSONArray jsonArray2 = jsonArray.getJSONObject(i).getJSONArray(key);
//                for (int j = 0; j < jsonArray2.size(); j++) {
//                    TreePoint treePoint2 = getTreePointFromJson(jsonArray2.getJSONObject(j), pid2, 2);
//                    coursesList.add(treePoint2);
//                    if (!treePoint2.isLeaf()) {
//                        int pid3 = treePoint2.getId();
//                        JSONArray jsonArray3 = jsonArray2.getJSONObject(j).getJSONArray(key);
//                        for (int k = 0; k < jsonArray3.size(); k++) {
//                            TreePoint treePoint3 = getTreePointFromJson(jsonArray3.getJSONObject(k), pid3, 3);
//                            coursesList.add(treePoint3);
//                            if (!treePoint3.isLeaf()) {
//                                int pid4 = treePoint3.getId();
//                                JSONArray jsonArray4 = jsonArray3.getJSONObject(k).getJSONArray(key);
//                                for (int l = 0; l < jsonArray4.size(); l++) {
//                                    TreePoint treePoint4 = getTreePointFromJson(jsonArray4.getJSONObject(l), pid4, 4);
//                                    coursesList.add(treePoint4);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    /**
     * 解析服务器传来的保存着章节信息的JSON数据
     * 通过递归的方式，深度优先遍历，
     * 把JSONObject解析为TreePoint
     * 并存储到coursesList中
     *
     * @param jsonArray
     * @param pid
     * @param level
     * @param key
     */
    private void loadTreePoint(JSONArray jsonArray, int pid, int level, String key) {
        for (int i = 0; i < jsonArray.size(); i++) {
            TreePoint treePoint = getTreePointFromJson(jsonArray.getJSONObject(i), pid, level);
            coursesList.add(treePoint);
            if (!treePoint.isLeaf()) {
                int newPid = treePoint.getId();
                JSONArray newJsonArray = jsonArray.getJSONObject(i).getJSONArray(key);
                loadTreePoint(newJsonArray, newPid, level + 1, key);
            }
        }
    }

    private TreePoint getTreePointFromJson(com.alibaba.fastjson.JSONObject jsonObject, int pid, int level) {
        int id = 0;
        String name = "";
        boolean isLeaf = false;
        try {
            id = jsonObject.getInteger("id");
            name = jsonObject.getString("head");
            isLeaf = jsonObject.getBoolean("checked");

        } catch (Exception e) {
            KLog.e(e.getMessage());
        }
        return new TreePoint(id, name, pid, isLeaf, level);
    }


    @OnClick({R.id.iv_back_arrow, R.id.btn_submit, R.id.ly_book_name})
    void doDealWithThem(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.btn_submit:
                uploadSelectedPoints(((MulitListAdapter) mAdapter).getSelectedPointsList());
                break;
            case R.id.ly_book_name:
                finish();
        }
    }

    private void uploadSelectedPoints(List<String> selectedPointList) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < selectedPointList.size(); i++) {
            stringBuffer.append(selectedPointList.get(i));
            if (i != selectedPointList.size()-1) {
                stringBuffer.append(",");
            }

        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("type", key);
            jsonObject.put("list", stringBuffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NormalApi.getInstance().getResult(MethodCode.SaveCoach, jsonObject, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    JSONObject backJson = new JSONObject(responseBody.string());
                    if (backJson.getBoolean("result")) {
                        toastMessage("课程设置成功");
                        Intent intent = new Intent(SetTutorialTriMultiListActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
