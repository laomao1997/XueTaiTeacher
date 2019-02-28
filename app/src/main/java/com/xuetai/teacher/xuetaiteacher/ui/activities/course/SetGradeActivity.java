package com.xuetai.teacher.xuetaiteacher.ui.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class SetGradeActivity extends AppCompatActivity {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.tv_textbook_title)
    TextView tvTextbookTitle;


    String id;
    String name;

    List<Map<String, Object>> gradeList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_choose);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        name = intent.getStringExtra("NAME");
        tvTextbookTitle.setText(name);

        getDateFromRemote();
    }

    private void getDateFromRemote() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version", name);
        } catch (Exception e) {

        }
        NormalApi.getInstance().getResult(MethodCode.QuestionGradeList, jsonObject, new Subscriber<ResponseBody>() {
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
                    getDateFromJSONArray(jsonArray);
                    initView();
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    // 解析服务端返回的json数据，并以HashMap存储在gradeList中
    private void getDateFromJSONArray(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<String, Object> map = new HashMap<>();
            try {
                map.put("id", jsonArray.getJSONObject(i).getInt("id"));
                map.put("name", jsonArray.getJSONObject(i).getString("name"));
            } catch (Exception e) {
                KLog.e(e);
            }
            gradeList.add(map);
        }
    }

    private void initView() {
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                gradeList,
                R.layout.item_textbook,
                new String[]{"name"},
                new int[]{R.id.name}
        );
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long item) {
                Intent intent = new Intent(SetGradeActivity.this, SetTutorialTriMultiListActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("VERSION", name);
                intent.putExtra("GRADE", "" + gradeList.get(position).get("name"));
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.iv_back_arrow, R.id.ly_book_name})
    void doDealWithThem(View view) {
        finish();
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
