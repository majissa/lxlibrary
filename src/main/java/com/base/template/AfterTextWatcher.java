package com.base.template;

import android.text.TextWatcher;

/**
 * Created by Administrator on 2015/11/12.
 */
public abstract class AfterTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}




