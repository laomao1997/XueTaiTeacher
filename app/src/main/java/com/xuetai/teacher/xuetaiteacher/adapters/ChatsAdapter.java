package com.xuetai.teacher.xuetaiteacher.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.models.Chat;
import com.xuetai.teacher.xuetaiteacher.ui.activities.PhotoReviewActivity;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    private static final int TEACHER_CHAT = 1;
    private static final int STUDENT_CHAT = 2;
    private static final int SYSTEM_CHAT = 3;
    private static final int TEACHER_IMAGE = 4;
    private static final int STUDENT_IMAGE = 5;

    private List<Chat> chatList;

    private Activity activity;

    private String studentAvatar;
    private String teacherAvatar;

    // 为每个数据项提供引用
    // 复杂数据项可能需要给每个数据项提供多个 View
    // 同时，一个数据项的多个视图都可以在这个 ViewHolder 中访问
    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private View view;
        private TextView textView;
        private ImageView avatarView;
        private TextView timeView;
        private RadiusImageView radiusImageView;

        public MyViewHolder(View v) {
            super(v);
            view = v;
            avatarView = v.findViewById(R.id.avatar);
            timeView = v.findViewById(R.id.time);
            try {
                textView = v.findViewById(R.id.text);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                radiusImageView = v.findViewById(R.id.image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 提供一个合适的构造函数（取决于数据集的类型）
    public ChatsAdapter(Activity activity, List<Chat> chatList, String studentAvatar, String teacherAvatar) {
        this.activity = activity;
        this.chatList = chatList;
        this.studentAvatar = studentAvatar;
        this.teacherAvatar = teacherAvatar;
    }

    // 创建新的 views （被 layout manager 调用）
    @NonNull
    @Override
    public ChatsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个新的 view
        View v;
        switch (viewType) {
            case TEACHER_CHAT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_right_chat, parent, false);
                break;
            case STUDENT_CHAT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_left_chat, parent, false);
                break;
            case SYSTEM_CHAT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_left_chat, parent, false);
                break;
            case TEACHER_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_right_chat_image, parent, false);
                break;
            case STUDENT_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_left_chat_image, parent, false);
                break;
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_left_chat, parent, false);
        }
        return new MyViewHolder(v);
    }

    public interface OnImageClickListener {
        void OnImageClick(int position);
    }

    OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    // 填充 view 中的内容（被 layout manager 调用）
    @Override
    public void onBindViewHolder(final ChatsAdapter.MyViewHolder holder, final int position) {
        if (chatList.get(position).getFrom().equals("1")) {
            Glide.with(activity).load(teacherAvatar).into(holder.avatarView);
        } else {
            Glide.with(activity).load(studentAvatar).into(holder.avatarView);
        }
        if (chatList.get(position).getType().equals("text")) {
            holder.textView.setText(chatList.get(position).getMessage());
        } else {
            Glide.with(activity).load(chatList.get(position).getMessage()).into(holder.radiusImageView);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageClickListener != null) {
                        onImageClickListener.OnImageClick(position);
                    }
                }
            });
        }
        holder.timeView.setText(stampToDate(chatList.get(position).getTimeStamp()));
        if (isChatContinued(position)) holder.timeView.setVisibility(View.GONE);
    }

    // 返回数据集的长度（被 layout manager 调用）
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = Integer.parseInt(chatList.get(position).getFrom());
        if (chatList.get(position).getType().equals("img")) {
            type += 3;
        }
        return type;
    }

    private static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt * 1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    // 判断这条消息与上一条消息的时间差
    // 小于5分钟，则返回真
    private boolean isChatContinued(int position) {
        if (position > 0) {
            return Integer.parseInt(chatList.get(position).getTimeStamp())
                    - Integer.parseInt(chatList.get(position - 1).getTimeStamp()) < 180;
        }
        return false;
    }


}
