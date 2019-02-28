package com.xuetai.teacher.xuetaiteacher.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.CommentAdapter;
import com.xuetai.teacher.xuetaiteacher.models.CommentBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EvaluationFragment extends Fragment {

    @BindView(R.id.main_lv)
    ListView listView;
    @BindView(R.id.empty_view)
    LinearLayout emptyLayout;

    List<CommentBean> commentBeanList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_evaluation, null);
        ButterKnife.bind(this, v);

        initView();
        return v;
    }

    public EvaluationFragment() {
    }

    public static EvaluationFragment newInstance(List<CommentBean> list) {
        EvaluationFragment evaluationFragment = new EvaluationFragment();
        evaluationFragment.commentBeanList = list;
        return evaluationFragment;
    }

    private void initView() {
        if (!commentBeanList.isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            CommentAdapter commentAdapter = new CommentAdapter(commentBeanList, getContext());
            listView.setAdapter(commentAdapter);
            listView.setDividerHeight(0);

            commentAdapter.setOnReplyClickListener(new CommentAdapter.OnReplyClickListener() {
                @Override
                public void OnReplyClick(int position) {
                    toastMessage("点击了 " + commentBeanList.get(position).getTime() + " 的回复");
                }
            });
        }
    }


    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
