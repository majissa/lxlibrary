package com.base.template;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.IdRes;

/**
 * Created by 李响
 * 创建日期 2017/3/29
 * 描述：
 */
public class MultipleTextWatcher implements TextWatcher {

    private
    @IdRes
    int editTextId;
    private AfterTextListener afterTextListener;

    public MultipleTextWatcher(int etId, AfterTextListener afterTextListener) {
        this.editTextId = etId;
        this.afterTextListener = afterTextListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        afterTextListener.onAferTextListener(s, editTextId);
    }

   public interface AfterTextListener {
        void onAferTextListener(Editable s, int etId);
    }
}
