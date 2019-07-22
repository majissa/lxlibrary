package com.base.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.adapter.BaseRecyclerAdapter;
import com.base.listener.OnCancelClickListener;

/**
 * 创建人：郑晓辉
 * 创建日期：2016/4/13
 * 描述：抽像弹出RecyclerViewPopupWindow
 * 注意:如果宽度不是充满屏幕，则RecyclerView的宽度要设置成固定值多少dp,Adapter的TextView的宽度要设置成Match.分割线有时候会出现问题，比如最后一条多出来等。所以还是推荐用ListViewPopupWindow
 * 除非是横向的必须用RecyclerViewPopupWindow
 *
 */
@Deprecated
public abstract class BaseRecyclerViewPopupWindow extends AlphaPopupWindow {

    private TextView tvTitle;
    private RecyclerView recyclerView;
    private TextView tvCancel;

    private OnCancelClickListener onCancelClickListener;

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public BaseRecyclerViewPopupWindow(Context context, View view) {
        super(context, view);
        init(view);
    }

    public BaseRecyclerViewPopupWindow(Context context, View view, int width, int height) {
        super(context, view, width, height);
        init(view);
    }

    private void init(View view) {
        int titleId = findTitleId();
        int RecyclerViewId = findRecyclerViewId();
        int cancelId = findCancelId();
        if (titleId != 0) {
            tvTitle = (TextView) view.findViewById(findTitleId());
        }
        if (RecyclerViewId != 0) {
            recyclerView = (RecyclerView) view.findViewById(findRecyclerViewId());
            recyclerView.setHasFixedSize(true);
            setLayoutManager(recyclerView);
        }
        if (cancelId != 0) {
            tvCancel = (TextView) view.findViewById(findCancelId());
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onCancelClickListener != null) {
                        onCancelClickListener.onCancelClick(v);
                    }
                }
            });
        }
    }

    //子类可以覆盖这个方法
    protected void setLayoutManager(RecyclerView recyclerView){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    protected abstract int findTitleId();

    protected abstract int findRecyclerViewId();

    protected abstract int findCancelId();

    public void setTitle(@StringRes int resid) {
        if (tvTitle != null) {
            tvTitle.setText(resid);
        }
    }

    public void setAdapter(BaseRecyclerAdapter zBaseRecyclerAdapter) {
        if (recyclerView != null) {
            recyclerView.setAdapter(zBaseRecyclerAdapter);
        }
    }

    public void setCancel(@StringRes int resid) {
        if (tvCancel != null) {
            tvCancel.setText(resid);
        }
    }


}
