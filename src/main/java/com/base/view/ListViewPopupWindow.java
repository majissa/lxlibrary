package com.base.view;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.base.adapter.BaseAdapterAdvance;
import com.base.listener.OnCancelClickListener;

/**
 * 创建人：郑晓辉
 * 创建日期：2016/4/13
 * 描述：抽像弹出ListViewPopupWindow
 * 注意:如果宽度不是充满屏幕，则ListView的宽度要设置成固定值多少dp,Adapter的TextView的宽度要设置成Match
 */
public class ListViewPopupWindow extends AlphaPopupWindow {

    private TextView tvTitle;
    private ListView listView;
    private TextView tvCancel;
    private int titleId;
    private int listViewId;
    private int cancelId;

    private OnCancelClickListener OnCancelClickListener;

    public void setOnCancelClickListener(OnCancelClickListener OnCancelClickListener) {
        this.OnCancelClickListener = OnCancelClickListener;
    }

    private ListViewPopupWindow(Context context, View view) {
        super(context, view);
    }

    private ListViewPopupWindow(Context context, View view, int width, int height) {
        super(context, view, width, height);
    }

    private void init(View view) {
        if (titleId != 0) {
            tvTitle = (TextView) view.findViewById(titleId);
        }
        if (listViewId != 0) {
            listView = (ListView) view.findViewById(listViewId);
        }
        if (cancelId != 0) {
            tvCancel = (TextView) view.findViewById(cancelId);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (OnCancelClickListener != null) {
                        OnCancelClickListener.onCancelClick(v);
                    }
                }
            });
        }
    }

    public void setTitle(@StringRes int resid) {
        if (tvTitle != null) {
            tvTitle.setText(resid);
        }
    }

    public void setAdapter(BaseAdapterAdvance zBaseAdapterAdvance) {
        if (listView != null) {
            listView.setAdapter(zBaseAdapterAdvance);
        }
    }

    public void setCancel(@StringRes int resid) {
        if (tvCancel != null) {
            tvCancel.setText(resid);
        }
    }

    public static class Builder {

        private ListViewPopupWindow listViewCancelPopupWindow;
        private View view;

        public Builder(Context context, View view) {
            listViewCancelPopupWindow = new ListViewPopupWindow(context, view);
            this.view = view;
        }

        public Builder(Context context, View view, int width, int height) {
            listViewCancelPopupWindow = new ListViewPopupWindow(context, view, width, height);
            this.view = view;
        }

        public Builder setTitleId(int titleId) {
            listViewCancelPopupWindow.titleId = titleId;
            return this;
        }

        public Builder setListViewId(int listViewId) {
            listViewCancelPopupWindow.listViewId = listViewId;
            return this;
        }

        public Builder setCancelId(int cancelId) {
            listViewCancelPopupWindow.cancelId = cancelId;
            return this;
        }

        public ListViewPopupWindow build() {
            listViewCancelPopupWindow.init(view);
            return listViewCancelPopupWindow;
        }
    }

}
