package com.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * 创建人：郑晓辉
 * 创建日期：2016/6/23
 * 描述：自带转圈圈的图片展示布局，未完待续。。。
 */
public class ProgressImageLayout extends FrameLayout {

    private ImageView imageView;
    private ProgressBar progressBar;

    public ProgressImageLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public ProgressImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProgressImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }


    private void init(AttributeSet attrs, int defStyle) {


        loadViews();
    }

    private void loadViews() {
        imageView=new ImageView(getContext());
        FrameLayout.LayoutParams imageLayoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLayoutParams.gravity= Gravity.CENTER;
        imageView.setLayoutParams(imageLayoutParams);

        progressBar=new ProgressBar(getContext());
        FrameLayout.LayoutParams progressLayoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressLayoutParams.gravity= Gravity.CENTER;
        progressBar.setLayoutParams(progressLayoutParams);

        addView(imageView);
        addView(progressBar);
    }
}
