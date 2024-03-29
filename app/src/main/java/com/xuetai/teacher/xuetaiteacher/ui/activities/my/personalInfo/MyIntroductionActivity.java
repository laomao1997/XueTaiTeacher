package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MyIntroductionActivity extends AppCompatActivity {

    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    SharedPreferencesHelper sharedPreferencesHelper;
    String info;
    String note;
    JSONObject jsonObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_introduction);

        // 设置沉浸式状态栏
        // 当 FitsSystemWindows 设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        // 设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        // 一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 会导致状态栏文字看不清
        // 所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            // 如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            // 这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

        ButterKnife.bind(this);

        sharedPreferencesHelper = new SharedPreferencesHelper(this, "setting");
        info = sharedPreferencesHelper.getSharedPreference("info", "no_info").toString();
        try {
            jsonObject = new JSONObject(info);
            note = jsonObject.getString("note");
        } catch (Exception e) {
            e.printStackTrace();
            note = "";
        }
        editText.setText(note);
    }

    @OnTextChanged(R.id.edit_text)
    void doOnTextChanged() {
        note = editText.getText().toString();
        tvNumber.setText("" + note.length() + "/1000");
    }


    @OnClick({R.id.iv_back_arrow, R.id.tv_submit})
    void doBack(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                System.out.println("note!!!!!!!");
                break;
            case R.id.iv_back_arrow:
                finish();
                break;
        }
    }

}
