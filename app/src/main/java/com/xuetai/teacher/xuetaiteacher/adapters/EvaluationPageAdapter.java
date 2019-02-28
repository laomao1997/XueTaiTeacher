package com.xuetai.teacher.xuetaiteacher.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.models.CommentBean;
import com.xuetai.teacher.xuetaiteacher.ui.fragments.EvaluationFragment;

import java.util.ArrayList;
import java.util.List;

public class EvaluationPageAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[]{"全部", "好评", "中评", "差评"};
    private int[] numbers = {0, 0, 0, 0};
    private Context context;
    private List<List<CommentBean>> lists = new ArrayList<>();

    private final List<Fragment> mFragment = new ArrayList<>();

    public EvaluationPageAdapter(FragmentManager fm,
                                 List<List<CommentBean>> lists,
                                 int[] numbers,
                                 Context context) {
        super(fm);
        this.lists = lists;
        this.numbers = numbers;
        this.context = context;
    }

    public void addFragment(Fragment fragment) {
        mFragment.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return EvaluationFragment.newInstance(lists.get(position));
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public View getCustomView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tab_evaluation, null);
        TextView textView = view.findViewById(R.id.tv_comment);
        TextView numberView = view.findViewById(R.id.tv_number);
        textView.setText(tabTitles[position]);
        numberView.setText(numbers[position]+"");
        return view;
    }

}
