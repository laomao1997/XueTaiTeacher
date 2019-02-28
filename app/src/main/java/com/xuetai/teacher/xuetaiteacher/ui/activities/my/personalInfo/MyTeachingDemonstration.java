package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xuetai.teacher.xuetaiteacher.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 教学展示
 */
public class MyTeachingDemonstration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teaching_demonstration);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }

}
