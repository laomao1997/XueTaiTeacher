package com.xuetai.teacher.xuetaiteacher.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.ui.activities.EducationInfoActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EducationGradeBottomSheetDialog extends BottomSheetDialogFragment {

    ClickListenerInterface clickListenerInterface;
    String gradeString;
    String selected;
    ArrayList<String> gradeSelectedList = new ArrayList<>();

    @BindView(R.id.box1)
    CheckBox mCheckBox1;

    @BindView(R.id.box2)
    CheckBox mCheckBox2;

    @BindView(R.id.box3)
    CheckBox mCheckBox3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_dialog_bottom_sheet_education_grade, container, false);
        ButterKnife.bind(this, v);
        setGradeString(gradeString);
        return v;
    }

    public void setGradeString(String string) {
        this.gradeString = string.substring(0, 1);
        mCheckBox1.setText(gradeString + "一");
        mCheckBox2.setText(gradeString + "二");
        mCheckBox3.setText(gradeString + "三");
    }

    private String getSelectedGrades() {
        gradeSelectedList.clear();
        String s = "";
        if (mCheckBox1.isChecked()) {
            s += gradeString + "一";
            gradeSelectedList.add("1");
        }
        if (mCheckBox2.isChecked()) {
            s += gradeString + "二";
            gradeSelectedList.add("2");
        }
        if (mCheckBox3.isChecked()) {
            s += gradeString + "三";
            gradeSelectedList.add("3");
        }

        switch (s.length()) {
            case 2:
                break;
            case 4:
                String s1;
                String s2;
                s1 = s.substring(0, 2);
                s2 = s.substring(2, 4);
                s = s1 + " " + s2;
                break;
            case 6:
                s = gradeString+ "一 " + gradeString + "二 " + gradeString + "三";
                break;
        }
        System.out.println(s);
        return s;
    }

    public ArrayList<String> getGradeSelectedList() {
        return gradeSelectedList;
    }

    public String getSelected() {
        return selected;
    }

    @OnClick(R.id.tv_btn_save)
    void doClick(View view) {
        switch (view.getId()) {
            case R.id.tv_btn_save: {
                selected = getSelectedGrades();
                clickListenerInterface.doResult();
                break;
            }
        }
        this.dismiss();
    }

    // 接口 - 用来暴露给外部的activity，顺势使之可以处理fragment内部的onclick方法
    public interface ClickListenerInterface {

        void doResult();

    }

    // 接口的实现，外界通过使用此方法便可以调用接口
    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        gradeString = ((EducationInfoActivity) context).getStagePicked();
    }
}
