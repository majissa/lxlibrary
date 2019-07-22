package com.base.decoration;

/**
 * 创建人：郑晓辉
 * 创建日期：2016/6/15
 * 描述：
 */
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class is from the v7 samples of the Android SDK. It's not by me!
 * <p/>
 * See the license above for details.
 */
public class LinearLayoutDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;

    private int mOrientation;

    private int leftPadding;
    private int rightPadding;

    public LinearLayoutDecoration(Context context) {
        this(context, LinearLayoutManager.VERTICAL, 0, 0, 0);
    }

    public LinearLayoutDecoration(Context context, @DrawableRes int id) {
        this(context, LinearLayoutManager.VERTICAL, id, 0, 0);
    }

    public LinearLayoutDecoration(Context context, @DrawableRes int id, int leftPadding, int rightPadding) {
        this(context, LinearLayoutManager.VERTICAL, id, leftPadding, rightPadding);
    }

    public LinearLayoutDecoration(Context context, int orientation, @DrawableRes int id, int leftPadding, int rightPadding) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        if (id == 0) {
            mDivider = a.getDrawable(0);
        } else {
            mDivider = context.getResources().getDrawable(id);
        }
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft() + leftPadding;
        final int right = parent.getWidth() - parent.getPaddingRight() - rightPadding;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            long itemId = parent.getLayoutManager().getItemViewType(child);
            if (itemId == 0) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            long itemId = parent.getLayoutManager().getItemViewType(child);
            if (itemId == 0) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position != 0 && position != state.getItemCount() - 1 && position != state.getItemCount()) {//去掉ZBaseRecyclerAdapter中默认添加的头部和尾部，占位不画分割线
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }
}
