package com.base.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.listener.OnCloseClickListener;
import com.base.listener.OnInputFinishListener;

/**
 * 创建人：郑晓辉
 * 创建日期：2016/8/18
 * 描述：抽像弹出密码输入框的PopupWindow
 */
public class PasswordPopupWindow extends AlphaPopupWindow {

    private PasswordEditText passwordEditText;
    private ImageView ivClose;
    private TextView tvForgetPassword;
    private int passwordEditTextId;
    private int closeId;
    private int forgetPasswordId;

    public PasswordEditText getPasswordEditText() {
        return passwordEditText;
    }

    private OnCloseClickListener onCloseClickListener;

    public void setOnCloseClickListener(OnCloseClickListener onCloseClickListener) {
        this.onCloseClickListener = onCloseClickListener;
    }

    public interface OnForgetPasswordClickListener {
        void onForgetPasswordClick(View v);
    }

    private OnForgetPasswordClickListener onForgetPasswordClickListener;

    public void setOnForgetPasswordClickListener(OnForgetPasswordClickListener onForgetPasswordClickListener) {
        this.onForgetPasswordClickListener = onForgetPasswordClickListener;
    }

    public void setOnInputFinishListener(OnInputFinishListener onInputFinishListener) {
        if (onInputFinishListener != null && passwordEditText != null) {
            dismiss();
            passwordEditText.setOnInputFinishListener(onInputFinishListener);
        }
    }

    private PasswordPopupWindow(Context context, View view) {
        super(context, view);
    }

    private PasswordPopupWindow(Context context, View view, int width, int height) {
        super(context, view, width, height);
    }

    private void init(View view) {
        setDark(true);
        setBackgroundDrawable(null);
        setOutsideTouchable(false);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowBackgroundTranslucence(false);//背景恢复
                if (passwordEditTextId != 0) {
                    passwordEditText.getText().clear();
                }
            }
        });
        if (passwordEditTextId != 0) {
            passwordEditText = (PasswordEditText) view.findViewById(passwordEditTextId);
            passwordEditText.setFocusable(true);// 这个很重要，如果没设置，onKey执行不到
            passwordEditText.setFocusableInTouchMode(true);
            passwordEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
        if (closeId != 0) {
            ivClose = (ImageView) view.findViewById(closeId);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onCloseClickListener != null) {
                        onCloseClickListener.onCloseClick(v);
                    }
                }
            });
        }
        if (forgetPasswordId != 0) {
            tvForgetPassword = (TextView) view.findViewById(forgetPasswordId);
            tvForgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onForgetPasswordClickListener != null) {
                        onForgetPasswordClickListener.onForgetPasswordClick(v);
                    }
                }
            });
        }
    }

    public static class Builder {

        private PasswordPopupWindow listViewCancelPopupWindow;
        private View view;

        public Builder(Context context, View view) {
            listViewCancelPopupWindow = new PasswordPopupWindow(context, view);
            this.view = view;
        }

        public Builder(Context context, View view, int width, int height) {
            listViewCancelPopupWindow = new PasswordPopupWindow(context, view, width, height);
            this.view = view;
        }

        public Builder setPasswordEditTextId(int passwordEditTextId) {
            listViewCancelPopupWindow.passwordEditTextId = passwordEditTextId;
            return this;
        }

        public Builder setCloseId(int closeId) {
            listViewCancelPopupWindow.closeId = closeId;
            return this;
        }

        public Builder setForgetPasswordId(int forgetPasswordId) {
            listViewCancelPopupWindow.forgetPasswordId = forgetPasswordId;
            return this;
        }

        public PasswordPopupWindow build() {
            listViewCancelPopupWindow.init(view);
            return listViewCancelPopupWindow;
        }
    }

}
