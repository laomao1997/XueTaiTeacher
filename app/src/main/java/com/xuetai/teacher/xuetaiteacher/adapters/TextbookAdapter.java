package com.xuetai.teacher.xuetaiteacher.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.models.course.TextBook;

import java.util.List;

/**
 * Author: Jinghao Zhang
 * Email: zhang.jing.hao@outlook.com
 * Date: 2019/1/25
 */
public class TextbookAdapter extends RecyclerView.Adapter<TextbookAdapter.TextbookViewHolder> {

    private List<TextBook> mDataset;

    public static class TextbookViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public TextbookViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.name);
        }
    }

    public TextbookAdapter(List<TextBook> myDataset) {
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public TextbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_textbook, parent, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextbookViewHolder vh = new TextbookViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull TextbookViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position).getName());

        // 点击监听
        if (mOnItemClickListener != null) {
            // 为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // 以下代码为了设置点击监听
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
