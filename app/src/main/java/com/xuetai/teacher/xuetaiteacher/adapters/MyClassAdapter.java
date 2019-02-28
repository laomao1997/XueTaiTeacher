package com.xuetai.teacher.xuetaiteacher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xuetai.teacher.xuetaiteacher.R;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyClassAdapter extends BaseAdapter {

    private List<Map<String, Object>> datas;
    private Context context;

    public MyClassAdapter(List<Map<String, Object>> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return datas.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        //判断依据
        try{
            if(datas.get(position).get("tag").toString().equals("1")) return 1;
            else if(datas.get(position).get("tag").toString().equals("3")) return 3;
            else return 0;
        }catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        //布局个数
        return 4;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        TextView textViewName;
        TextView textViewTime;
        TextView textViewAction;
        ImageView circleImageView;
        int type = getItemViewType(position);
        switch (type) {
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_single_classes, parent, false);
                textViewName = view.findViewById(R.id.tv_student_name);
                textViewTime = view.findViewById(R.id.tv_begin_time);
                textViewAction = view.findViewById(R.id.tv_actiondesc);
                circleImageView = view.findViewById(R.id.iv_avatar);
                textViewName.setText(datas.get(position).get("studentName").toString());
                textViewTime.setText(datas.get(position).get("beginTime").toString());
                textViewAction.setText(datas.get(position).get("actionDesc").toString());
                try {
                    String avatarUrl = datas.get(position).get("studentHead").toString();
                    if (!avatarUrl.isEmpty()) {
                        Glide.with(context).load(avatarUrl).into(circleImageView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_special_classes, parent, false);
                textViewName = view.findViewById(R.id.tv_student_name);
                textViewTime = view.findViewById(R.id.tv_begin_time);
                textViewAction = view.findViewById(R.id.tv_actiondesc);
                textViewName.setText(datas.get(position).get("studentName").toString());
                textViewTime.setText(datas.get(position).get("beginTime").toString());
                textViewAction.setText(datas.get(position).get("actionDesc").toString());
                break;
        }
        return view;
    }

}
