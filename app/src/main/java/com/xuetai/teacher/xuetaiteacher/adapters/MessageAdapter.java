package com.xuetai.teacher.xuetaiteacher.adapters;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xuetai.teacher.xuetaiteacher.R;

import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Map<String, Object>> data;
    private Activity activity;

    // 为每个数据项提供引用
    // 复杂数据项可能需要给每个数据项提供多个 View
    // 同时，一个数据项的多个视图都可以在这个 ViewHolder 中访问
    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private View view;
        ImageView ivAvatar;
        TextView tvNickName;
        TextView tvChat;
        TextView tvTime;
        TextView tvNumber;

        public MyViewHolder(View v) {
            super(v);
            view = v;
            ivAvatar = view.findViewById(R.id.iv_avatar);
            tvNickName = view.findViewById(R.id.tv_nick_name);
            tvChat = view.findViewById(R.id.tv_chat);
            tvTime = view.findViewById(R.id.tv_time);
            tvNumber = view.findViewById(R.id.tv_number);
        }
    }

    public interface OnItemClickListener {
        void OnImageClick(int position);
    }

    OnItemClickListener onItemClickListener;

    public void setOnImageClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MessageAdapter(Activity activity, List<Map<String, Object>> data) {
        this.activity = activity;
        this.data = data;
    }


    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个新的 view
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message, parent, false);
        return new MyViewHolder(v);
    }

    // 填充 view 中的内容（被 layout manager 调用）
    @Override
    public void onBindViewHolder(final MessageAdapter.MyViewHolder holder, final int position) {
        Glide.with(activity).load(data.get(position).get("headPhoto")).into(holder.ivAvatar);
        holder.tvNickName.setText("" + data.get(position).get("studentName"));
        holder.tvChat.setText("" + data.get(position).get("lastMessage"));
        holder.tvTime.setText("" + data.get(position).get("updatedTime"));
        String number = "" + data.get(position).get("unread");
        if (number.equals("0")) holder.tvNumber.setVisibility(View.GONE);
        else {
            holder.tvNumber.setText(number);
            holder.tvNumber.setVisibility(View.VISIBLE);
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnImageClick(position);
                }
            }
        });
    }

    // 返回数据集的长度（被 layout manager 调用）
    @Override
    public int getItemCount() {
        return data.size();
    }

}
