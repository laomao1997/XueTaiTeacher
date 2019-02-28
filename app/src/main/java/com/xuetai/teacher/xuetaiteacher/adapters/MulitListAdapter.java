package com.xuetai.teacher.xuetaiteacher.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.models.TreePoint;

import java.util.ArrayList;
import java.util.List;

public class MulitListAdapter extends RecyclerView.Adapter<MulitListAdapter.MyViewHolder> {

    private static final String TAG = "MulitListAdapter";

    private List<TreePoint> mDataset;
    private List<TreePoint> treePointList;
    private List<TreePoint> treePointsSelected = new ArrayList<>();

    private int numOfSelected = 0;

    private static final int FIRST_TYPE = 1;
    private static final int SECOND_TYPE = 2;
    private static final int THIRD_TYPE = 3;
    private static final int FOURTH_TYPE = 4;

    // 为每个数据项提供引用
    // 复杂数据项可能需要给每个数据项提供多个 View
    // 同时，一个数据项的多个视图都可以在这个 ViewHolder 中访问
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private TextView textView;

        public MyViewHolder(View v) {
            super(v);
            view = v;
            imageView = v.findViewById(R.id.image);
            textView = v.findViewById(R.id.text);
        }
    }

    // 提供一个合适的构造函数（取决于数据集的类型）
    public MulitListAdapter(List<TreePoint> treePointList) {
        this.treePointList = treePointList;
        mDataset = new ArrayList<>();
        for (TreePoint treePoint : this.treePointList) {
            if (treePoint.getLevel() == 1) {
                mDataset.add(treePoint);
            }
        }
    }

    // 创建新的 views （被 layout manager 调用）
    @Override
    public MulitListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个新的 view
        View v;
        if (viewType == FIRST_TYPE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expan_first, parent, false);
        } else if (viewType == SECOND_TYPE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expan_second, parent, false);
        } else if (viewType == THIRD_TYPE){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expan_third, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expan_fourth, parent, false);
        }
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // 填充 view 中的内容（被 layout manager 调用）
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        bindTreePoint(holder, mDataset.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClick(holder, position, mDataset.get(position));
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, mDataset.get(position));
                }
            }
        });
    }


    // 返回数据集的长度（被 layout manager 调用）
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position).getLevel();
    }

    //点击 RecyclerView 某条的监听
    public interface OnItemClickListener {
        /**
         * 当RecyclerView某个被点击的时候回调
         *
         * @param view      点击item的视图
         * @param treePoint 点击得到的数据
         */
        void onItemClick(View view, TreePoint treePoint);
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 设置RecyclerView某个的监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void printMDataSet() {
        System.out.println("====================");
        for (TreePoint treePoint : mDataset) {
            System.out.println(treePoint);
        }
    }

    public int getNumOfSelected() {
        return numOfSelected;
    }

    private void bindTreePoint(final MyViewHolder holder, TreePoint treePoint) {
        holder.textView.setText(treePoint.getName());
        if (treePoint.isLeaf()) {
            if (treePoint.isSelected())
                setSelected(holder);
            else
                setUnselected(holder);
        } else {
            if (treePoint.isExpand())
                setExpanded(holder);
            else setUnexpanded(holder);
        }
    }

    private void doClick(final MyViewHolder holder, final int position, TreePoint treePoint) {
        System.out.println("点击了 " + treePoint.getName());
        if (treePoint.isLeaf()) {
            if (treePoint.isSelected()) {
//                setUnselected(holder);
                treePoint.setSelected(false);
                numOfSelected--;
            } else {
//                setSelected(holder);
                if (numOfSelected < 10) {
                    treePoint.setSelected(true);
                    numOfSelected++;
                }
            }
            notifyItemChanged(position);
        } else {
            if (treePoint.isExpand()) {
                treePoint.setExpand(false);
                shrinkItems(holder, position, treePoint);
            } else {
                treePoint.setExpand(true);
                expandItems(holder, position, treePoint);
            }
        }
    }

    private void shrinkItems(final MyViewHolder holder, final int position, TreePoint treePoint) {
        setUnexpanded(holder);
        int numRemoved = 0;
        for (int i = position + 1; i < mDataset.size(); i++) {
            if (mDataset.get(i).getLevel() <= treePoint.getLevel()) break;
            else {
                numRemoved++;
            }
        }
        for (int k = 0; k < numRemoved; k++) {
            mDataset.remove(position + 1);
            notifyItemRemoved(position + 1);
        }
        notifyItemRangeChanged(0, mDataset.size());
    }

    private void expandItems(final MyViewHolder holder, final int position, TreePoint treePoint) {
        setExpanded(holder);
        int numInserted = 0;
        for (int i = treePointList.size() - 1; i >= 0; i--) {
            if (treePoint.getId() == treePointList.get(i).getPid()) {
                if (treePointList.get(i).isExpand()) {
                    for (int j = treePointList.size() - 1; j >= 0; j--) {
                        if (treePointList.get(j).getPid() == treePointList.get(i).getId()) {
                            numInserted++;
                            mDataset.add(position + 1, treePointList.get(j));
                            notifyItemInserted(position+1);
                        }
                    }
                }
                numInserted++;
                mDataset.add(position + 1, treePointList.get(i));
                notifyItemInserted(position+1);
            }
        }
        notifyItemRangeChanged(0, mDataset.size());
    }

    //获取被选中ID列表
    public List<String> getSelectedPointsList() {
        List<String> selectedPointList = new ArrayList<>();
        for (TreePoint treePoint : treePointList) {
            if (treePoint.isSelected()) {
                selectedPointList.add("" + treePoint.getId());
            }
        }
        return selectedPointList;
    }

    private void setSelected(final MyViewHolder holder) {
        holder.imageView.setImageResource(R.drawable.checkbox_checked);
        holder.textView.setTextColor(Color.parseColor("#0099FF"));
    }

    private void setUnselected(final MyViewHolder holder) {
        holder.imageView.setImageResource(R.drawable.checkbox_normal);
        holder.textView.setTextColor(Color.parseColor("#606060"));
    }

    private void setExpanded(final MyViewHolder holder) {
        holder.imageView.setImageResource(R.drawable.ic_expanded_true);
        holder.textView.setTextColor(Color.parseColor("#606060"));
    }

    private void setUnexpanded(final MyViewHolder holder) {
        holder.imageView.setImageResource(R.drawable.ic_expanded_false);
        holder.textView.setTextColor(Color.parseColor("#606060"));
    }
}
