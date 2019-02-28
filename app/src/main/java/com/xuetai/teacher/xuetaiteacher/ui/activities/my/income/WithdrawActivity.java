package com.xuetai.teacher.xuetaiteacher.ui.activities.my.income;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.utils.BankCardTextWatcher;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import rx.Subscriber;


public class WithdrawActivity extends AppCompatActivity {

    int withdrawType = 1;
    boolean withdrawSuccess = true;
    String balance;

    SharedPreferencesHelper sharedPreferencesHelper;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_number)
    ImageView ivNumber;
    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.iv_x_number)
    ImageView ivNumberX;
    @BindView(R.id.iv_x_amount)
    ImageView ivAmountX;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_withdraw);
        setStatusBar();

        ButterKnife.bind(this);
        setView();

        getReference();
    }

    private void setStatusBar() {
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
    }

    private void setView() {
        // 提现方式 0-支付宝 1-银行卡
        Intent intent = getIntent();
        withdrawType = intent.getIntExtra("WITHDRAW_TYPE", 1);
        balance = intent.getStringExtra("BALANCE");
        if (withdrawType == 1) {
            tvTitle.setText("提现到支付宝");
//            Resources resources = this.getResources();
//            Drawable drawable = resources.getDrawable(R.drawable.ic_alipay);
//            ivNumber.setImageDrawable(drawable);
            etNumber.setHint("支付宝账号");
//            etNumber.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else {
            tvTitle.setText("提现到银行卡");
            Resources resources = this.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.ic_bankcard);
            ivNumber.setImageDrawable(drawable);
            etNumber.setHint("银行卡号");
            etNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
            InputFilter[] filters = {new InputFilter.LengthFilter(23)};
            etNumber.setFilters(filters);
            BankCardTextWatcher.bind(etNumber);
        }
        tvBalance.setText(balance);
    }

    @OnTextChanged(R.id.et_number)
    void setListenNumber() {
        if (etNumber.getText().length() == 0) ivNumberX.setVisibility(View.GONE);
        else ivNumberX.setVisibility(View.VISIBLE);

        btnConfirm.setEnabled(isInfoOk());
    }

    @OnTextChanged(R.id.et_amount)
    void setListenAmount() {
        if (etAmount.getText().length() == 0) ivAmountX.setVisibility(View.GONE);
        else ivAmountX.setVisibility(View.VISIBLE);

        btnConfirm.setEnabled(isInfoOk());
    }

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }

    @OnClick({R.id.iv_x_number, R.id.iv_x_amount})
    void doClear(View v) {
        switch (v.getId()) {
            case R.id.iv_x_number:
                etNumber.setText("");
                break;
            case R.id.iv_x_amount:
                etAmount.setText("");
                break;
        }
    }

    private boolean isInfoOk() {
        return !etAmount.getText().toString().isEmpty()
                && !etNumber.getText().toString().isEmpty()
                && (withdrawType == 1 || etNumber.getText().length() == 23);
    }

    private int getWithdrawAmount() {
        return Integer.parseInt(etAmount.getText().toString());
    }

    @OnClick(R.id.btn_confirm)
    void doConfirm() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", etNumber.getText().toString());
            jsonObject.put("type", withdrawType);
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.AddTeacherAccountNum, jsonObject, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    int code = new JSONObject(responseBody.string()).getInt("code");
                    withdrawSuccess = (code != 16);
                    if (withdrawSuccess) {
                        setReference();

                        JSONObject jsonParams = new JSONObject();
                        try {
                            jsonParams.put("money", getWithdrawAmount());
                        } catch (Exception e) {
                            KLog.e(e);
                        }
                        NormalApi.getInstance().getResult(MethodCode.WithdrawApply, jsonParams, new Subscriber<ResponseBody>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    com.alibaba.fastjson.JSONObject jsonObject1 = JSON.parseObject(responseBody.string());
                                    KLog.json("提现结果", jsonObject1.toJSONString());
                                    int code = jsonObject1.getInteger("code");
                                    if (code == 16) showUnsuccessDialog("有未处理的提现申请");
                                    else showSuccessDialog();
                                } catch (Exception e) {
                                    KLog.e(e);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    /**
     * 显示提现申请成功对话框
     */
    void showSuccessDialog() {
//        new MaterialDialog.Builder(getApplicationContext())
//                .customView(R.layout.dialog_withdraw_income, true)
//                .show();
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(WithdrawActivity.this);
        final View dialogView = LayoutInflater.from(WithdrawActivity.this)
                .inflate(R.layout.dialog_withdraw_income,null);
        customizeDialog.setView(dialogView);
        customizeDialog.show();
    }

    /**
     * 显示提现不成功对话框
     * @param info 不成功信息
     */
    void showUnsuccessDialog(String info) {
        CookieBar.builder(this)
                .setTitle("提现申请不成功")
                .setMessage(info)
                .setDuration(3000)
                .setBackgroundColor(R.color.colorRed)
                .setActionColor(android.R.color.white)
                .setTitleColor(android.R.color.white)
                .setAction("点击确认", view -> {

                })
                .show();
    }

    // 将提现账户存入本地
    private void setReference() {
        sharedPreferencesHelper = new SharedPreferencesHelper(this, "setting");
        sharedPreferencesHelper.put("withdraw_account", etNumber.getText().toString());
        sharedPreferencesHelper.put("withdraw_type", withdrawType);
    }

    // 获取本地已有体现账户信息
    private void getReference() {
        sharedPreferencesHelper = new SharedPreferencesHelper(this, "setting");
        String number = (String) sharedPreferencesHelper.getSharedPreference("withdraw_account", "");
        int type = (int) sharedPreferencesHelper.getSharedPreference("withdraw_type", 0);
        if (!number.isEmpty() && type == withdrawType) {
            etNumber.setText(number);
            etAmount.requestFocus(); // 使金额EditText获取焦点
        }
    }

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
