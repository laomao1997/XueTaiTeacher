package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.xuetai.teacher.xuetaiteacher.ui.activities.MainActivity;
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
    RecyclerView listView;

    MessageAdapter messageAdapter;

    String messageJsonArgs = "";

    int unreadTotal = 0;

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
        initView();

        return v;
    }

    private void loadDialogues() {
        ((MainActivity) getActivity()).SetOnMessageUpdateListener(new MainActivity.OnMessageUpdateListener() {
            @Override
            public void onMessageUpdate(String message) {
                data.clear();
                try {
                    JSONArray jsonArray = JSON.parseObject(message).getJSONArray("result");
                    KLog.json(jsonArray.toJSONString());

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
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    int number = 0;
                    for (int i = 0; i < data.size(); i++) {
                        number += Integer.parseInt((String) data.get(i).get("unread"));
                    }
                    KLog.a(number);
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initView() {
        messageAdapter = new MessageAdapter(getActivity(), data);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        messageAdapter.setOnImageClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void OnImageClick(int position) {
                getChatDetail(position);
            }
        });
    }

    private void getChatDetail(int position) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("STUDENT_ID", (int) data.get(position).get("studentId"));
        intent.putExtra("STUDENT_AVATAR", (String) data.get(position).get("headPhoto"));
        startActivity(intent);
    }

}
