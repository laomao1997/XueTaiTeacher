package com.xuetai.teacher.xuetaiteacher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.models.CommentBean;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private List<CommentBean> commentList;
    private Context context;

    public CommentAdapter(List<CommentBean> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (commentList.get(position).hasReply())
            return 1;
        else
            return 0;
    }

    @Override
    public int getViewTypeCount() {
        //布局个数
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ImageView imageFace;
        TextView textName;
        TextView textDate;
        TextView textComment;
        TextView textReply;
        CommentBean comment = commentList.get(position);
        switch (getItemViewType(position)) {
            //没有回复的

            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_evaluation, null);
                switch (Integer.parseInt(comment.getScore())) {
                    case 3:
                        ((ImageView) view.findViewById(R.id.iv_avatar)).setImageResource(R.drawable.ic_good_comment);
                        break;
                    case 2:
                        ((ImageView) view.findViewById(R.id.iv_avatar)).setImageResource(R.drawable.ic_medium_comment);
                        break;
                    case 1:
                        ((ImageView) view.findViewById(R.id.iv_avatar)).setImageResource(R.drawable.ic_bad_comment);
                        break;
                }
                ((TextView) view.findViewById(R.id.tv_nick_name)).setText(comment.getStudentName());
                ((TextView) view.findViewById(R.id.tv_date)).setText(comment.getTime());
                ((TextView) view.findViewById(R.id.tv_evaluation)).setText(comment.getComment());
                view.findViewById(R.id.tv_reply).setOnClickListener(v -> {
                    if (onReplyClickListener != null) {
                        onReplyClickListener.OnReplyClick(position);
                    }
                });
                break;
            //已经回复的
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_evaluation_replyed, null);
                switch (Integer.parseInt(comment.getScore())) {
                    case 3:
                        ((ImageView) view.findViewById(R.id.iv_avatar)).setImageResource(R.drawable.ic_good_comment);
                        break;
                    case 2:
                        ((ImageView) view.findViewById(R.id.iv_avatar)).setImageResource(R.drawable.ic_medium_comment);
                        break;
                    case 1:
                        ((ImageView) view.findViewById(R.id.iv_avatar)).setImageResource(R.drawable.ic_bad_comment);
                        break;
                }
                ((TextView) view.findViewById(R.id.tv_nick_name)).setText(comment.getStudentName());
                ((TextView) view.findViewById(R.id.tv_date)).setText(comment.getTime());
                ((TextView) view.findViewById(R.id.tv_evaluation)).setText(comment.getComment());
                ((TextView) view.findViewById(R.id.tv_reply)).setText(comment.getReply());
                break;
        }
        return view;
    }

    public interface  OnReplyClickListener {
        void OnReplyClick(int position);
    }

    private OnReplyClickListener onReplyClickListener;

    public void setOnReplyClickListener(OnReplyClickListener onReplyClickListener) {
        this.onReplyClickListener = onReplyClickListener;
    }
}
