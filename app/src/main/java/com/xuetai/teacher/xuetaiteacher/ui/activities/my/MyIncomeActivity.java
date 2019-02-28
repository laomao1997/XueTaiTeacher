package com.xuetai.teacher.xuetaiteacher.ui.activities.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.ui.activities.my.income.WithdrawActivity;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class MyIncomeActivity extends AppCompatActivity {

    String balance;

    @BindView(R.id.tv_income_sum)
    TextView mTvIncomeSum;
    @BindView(R.id.tv_balance)
    TextView mTvBalance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        setStatusBar();

        ButterKnife.bind(this);

        loadBalance();

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

    private void loadBalance() {
        NormalApi.getInstance().getResult(MethodCode.TeacherIncome, new JSONObject(), new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    JSONObject resultJson = new JSONObject(responseBody.string());
                    KLog.json("我的收入", resultJson.toString());
                    loadIncomeSumIntoTextView(resultJson.getJSONObject("result").getString("incomeSum"));
                    loadBalanceIntoTextView(resultJson.getJSONObject("result").getString("balance"));
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    @OnClick({R.id.iv_back_arrow, R.id.tv_instruction, R.id.ly_alipay_withdraw,
            R.id.ly_card_withdraw, R.id.ly_income_details, R.id.ly_withdraw_details})
    void doDealWithThem(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
            case R.id.tv_instruction:
                break;
            case R.id.ly_alipay_withdraw:
                Intent intent = new Intent(MyIncomeActivity.this, WithdrawActivity.class);
                intent.putExtra("BALANCE", balance);
                intent.putExtra("WITHDRAW_TYPE", 1);
                startActivity(intent);
                break;
            case R.id.ly_card_withdraw:
                Intent intent2 = new Intent(MyIncomeActivity.this, WithdrawActivity.class);
                intent2.putExtra("BALANCE", balance);
                intent2.putExtra("WITHDRAW_TYPE", 2);
                startActivity(intent2);
                break;
            case R.id.ly_income_details:
                break;
            case R.id.ly_withdraw_details:
                break;
        }
    }

    /**
     * 显示余额
     * @param balance
     */
    private void loadBalanceIntoTextView(String balance) {
        mTvBalance.setText(balance);
        this.balance = balance;
    }

    private void loadIncomeSumIntoTextView(String income) {
        mTvIncomeSum.setText(income);
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
