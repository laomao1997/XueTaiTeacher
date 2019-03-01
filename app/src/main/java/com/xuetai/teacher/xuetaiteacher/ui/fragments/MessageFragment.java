package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.MessageAdapter;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.ui.activities.ChatActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.PersonalActivity;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class MessageFragment extends Fragment {

    @BindView(R.id.listview)
    ListView listView;

    MessageAdapter messageAdapter;

    String messageJsonArgs = "";

    private List<Map<String, Object>> data = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadDialogues();
//        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_message, null);

        ButterKnife.bind(this, v);


        return v;
    }



    private void loadDialogues() {
        data.clear();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "setting");
        messageJsonArgs = sharedPreferencesHelper.getSharedPreference("MESSAGES", "NO").toString();
        try {
            JSONArray jsonArray = JSON.parseObject(messageJsonArgs).getJSONArray("result");
//                    KLog.json(jsonArray.toJSONString());
            for (int i = 0; i < jsonArray.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("studentId", jsonObject.getInteger("studentId"));
                item.put("studentName", jsonObject.getString("studentName"));
                item.put("lastMessage", jsonObject.getString("lastMessage"));
                item.put("updatedTime", jsonObject.getString("updatedTime"));
                item.put("headPhoto", jsonObject.getString("headPhoto"));
                item.put("unread", jsonObject.getString("unread"));
                data.add(item);
            }
        } catch (Exception e) {
            KLog.e(e);
        }
        initView();
    }

    private void initView() {
        messageAdapter = new MessageAdapter(getContext(), R.layout.list_item_message, data);
        listView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getChatDetail(position);
            }
        });
    }

    private void getChatDetail(int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", data.get(position).get("studentId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NormalApi.getInstance().getResult(MethodCode.GetChartDetaile, jsonObject, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    KLog.json(responseBody.string());
                    removeUnread(position);
//                    System.out.println("点击了" + data.get(position).get("studentName"));
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeUnread(int position) {
        data.get(position).put("unread", "0");
        messageAdapter.notifyDataSetChanged();
    }

}
