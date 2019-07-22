package com.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.base.R;


/**
 * Created by 李响
 * 创建日期 2017/2/20
 * 描述：可变化颜色的标题布局
 */
public class AlphaRelativeLayout extends RelativeLayout {
    private float alphaSpeed;
    private static final double DEFAULT_SPEED_VALUE = 0.15;//数值越大，渐变的越快
    private int titleHeight;
    private Context context;
    public float alphValue;

    public AlphaRelativeLayout(Context context) {
        super(context);
        init(context, null);
    }

    public AlphaRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AlphaRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlphaLinearlayout);
        alphaSpeed = typedArray.getFloat(R.styleable.AlphaLinearlayout_alphaSpeed, (float) DEFAULT_SPEED_VALUE);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        titleHeight = getMeasuredHeight();
    }

    /**
     * 设置颜色渐变从设置颜色变成透明
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
        alphValue = (int) (255 - (f * 255));
        if (childCount > 0) {
            for (int index = 0; index < childCount; index++) {
                if (getChildAt(index).getAlpha() == 0) {

                }
                getChildAt(index).setAlpha(alphValue);
            }
        }

        getBackground().setAlpha((int) alphValue);
        invalidate();
    }

    /**
     * 设置颜色渐变从透明变成设置颜色
     *
     * @param value
     */
    public void setGradient(float value) {
        if (value < 0) {
            value = 0;
        }
        float f = (alphaSpeed * (value / (float) (titleHeight)));//乘以0.15是为减少速度
        if (f > 1) {
            f = 1;
        }
        alphValue = f * 255;
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int index = 0; index < childCount; index++) {
                getChildAt(index).setAlpha(alphValue);
            }
        }

        getBackground().setAlpha((int) (alphValue));
        invalidate();
    }

    public float getAlphValue() {
        return alphValue;
    }
}
