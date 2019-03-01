package com.xuetai.teacher.xuetaiteacher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xuetai.teacher.xuetaiteacher.R;

import java.util.List;
import java.util.Map;

public class MessageAdapter extends ArrayAdapter {

    private int resourceId;
    private List<Map<String, Object>> data;

    public MessageAdapter(Context context, int resource, List<Map<String, Object>> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.data = objects;
    }

    public void resetData(List<Map<String, Object>> objects) {
        this.data = objects;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            ImageView ivAvatar = view.findViewById(R.id.iv_avatar);
            TextView tvNickName = view.findViewById(R.id.tv_nick_name);
            TextView tvChat = view.findViewById(R.id.tv_chat);
            TextView tvTime = view.findViewById(R.id.tv_time);
            TextView tvNumber = view.findViewById(R.id.tv_number);
            Glide.with(getContext()).load(data.get(position).get("headPhoto")).into(ivAvatar);
            tvNickName.setText("" + data.get(position).get("studentName"));
            tvChat.setText("" + data.get(position).get("lastMessage"));
            tvTime.setText("" + data.get(position).get("updatedTime"));
            String number = "" + data.get(position).get("unread");
            if (number.equals("0")) tvNumber.setVisibility(View.GONE);
            else {
                tvNumber.setText(number);
                tvNumber.setVisibility(View.VISIBLE);
            }
        } else {
            view = convertView;
        }
        return view;
    }
}
