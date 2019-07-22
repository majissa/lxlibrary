package com.base.strategy;

import android.content.Context;

import com.base.R;
import com.base.util.PopUtil;

/**
 * Created by Administrator on 2015/2/10.
 */
public class PopOne extends BasePop {

    /**
     * 系统默认对话框样式，带文字的
     * @param context
     */
    @Override
    public void showProgressDialog(Context context) {
        PopUtil.showProgressDialog(context, "", context.getString(R.string.progress_dialog_message));
    }
}
