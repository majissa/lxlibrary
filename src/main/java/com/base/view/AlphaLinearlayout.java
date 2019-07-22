package com.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.base.R;


/**
 * Created by 李响
 * 创建日期 2017/2/20
 * 描述：可变化颜色的标题布局
 */
public class AlphaLinearlayout extends LinearLayout {
    private float alphaSpeed;
    private static final double DEFAULT_SPEED_VALUE = 0.15;//数值越大，渐变的越快
    private int titleHeight;
    private Context context;

    public AlphaLinearlayout(Context context) {
        super(context);
        init(context, null);
    }

    public AlphaLinearlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AlphaLinearlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlphaLinearlayout);
        alphaSpeed = typedArray.getFloat(R.styleable.AlphaLinearlayout_alphaSpeed, (float) DEFAULT_SPEED_VALUE);
        typedArray.recycle();
        titleHeight = getMeasuredHeight();
    }


    /**
     * 设置高度
     *
     * @param value
     */
    public void setAlpha(float value) {
        if (value < 0) {
            value = 0;
        }
        float f = (alphaSpeed * (value / (float) (titleHeight)));//乘以0.15是为减少速度
        if (f > 1) {
            f = 1;
        }
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int index = 0; index < childCount; index++) {
                getChildAt(childCount).setAlpha((int) (f * 255));
            }
        }

        getBackground().setAlpha((int) (f * 255));
        invalidate();
    }
}
