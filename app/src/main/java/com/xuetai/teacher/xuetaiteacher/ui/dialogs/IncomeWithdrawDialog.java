package com.xuetai.teacher.xuetaiteacher.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.xuetai.teacher.xuetaiteacher.R;

public class IncomeWithdrawDialog extends Dialog {

    private Context mContext;

    public IncomeWithdrawDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public IncomeWithdrawDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_withdraw_income);

    }


}
