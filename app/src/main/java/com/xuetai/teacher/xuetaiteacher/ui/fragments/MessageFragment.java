package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.ui.activities.PersonalActivity;

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

public class MessageFragment extends Fragment {

    @BindView(R.id.listview)
    ListView listView;

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private SimpleAdapter simpleAdapter;

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

        View v =  inflater.inflate(R.layout.fragment_message, null);

        ButterKnife.bind(this, v);



        return v;
    }

    private void loadDialogues() {
        data.clear();
        NormalApi.getInstance().getResult(MethodCode.GetDialogues, new JSONObject(), new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try{
                    JSONArray jsonArray = JSON.parseObject(responseBody.string()).getJSONArray("result");
//                    KLog.json(jsonArray.toJSONString());
                    for(int i = 0; i < jsonArray.size(); i++) {
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
        });
    }

    private void initView() {
        simpleAdapter = new SimpleAdapter(
                getContext(),
                data,
                R.layout.list_item_message,
                new String[] {"studentName", "lastMessage", "updatedTime", "unread"},
                new int[] {R.id.tv_nick_name, R.id.tv_chat, R.id.tv_time, R.id.tv_number}
        );

        listView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }



}
