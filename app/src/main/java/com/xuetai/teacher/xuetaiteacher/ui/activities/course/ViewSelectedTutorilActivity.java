package com.xuetai.teacher.xuetaiteacher.ui.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class ViewSelectedTutorilActivity extends AppCompatActivity {

    String id;
    StringBuffer datas = new StringBuffer();

    @BindView(R.id.tv_datas)
    TextView tvDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selected_tutorial);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        initDatas();
    }

    private void initDatas() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NormalApi.getInstance().getResult(MethodCode.SelectedPointTree, jsonObject, new Subscriber<ResponseBody>() {
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
                    KLog.json(resultJson.toString());
                    String parentNodeKey = resultJson.getString("parentNodeKey");
                    JSONArray jsonArray = resultJson.getJSONArray("data");
                    generateTreeNodeFromJson(jsonArray, parentNodeKey, 0);
                    System.out.println(datas);
                    tvDatas.setText(datas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void generateTreeNodeFromJson(JSONArray jsonArray, String node, int level) {
        for (int ooo = 0; ooo < level; ooo++) {
            datas.append("    ");
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String head = jsonArray.getJSONObject(i).getString("head");
                JSONArray newJsonArray = jsonArray.getJSONObject(i).getJSONArray(node);
                if (newJsonArray.length() != 0) {
                    datas.append("> ").append(head).append("\n\n\n");
                    generateTreeNodeFromJson(newJsonArray, node, level+1);
                }
                else {
                    datas.append("✓ ").append(head).append("\n\n\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }

}
