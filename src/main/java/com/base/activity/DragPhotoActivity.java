package com.base.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.base.R;
import com.base.adapter.BaseViewpagerAdapter;
import com.base.util.ImageUtil;
import com.base.view.DragPhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class DragPhotoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    public static String IMAGE_PATH = "image_path";
    public static String DEFAULT_RESID = "default_resid";
    public static String POSITION = "position";
    private List<String> mList;
//    private DragPhotoView[] mPhotoViews;

    //    int mOriginLeft;
    ArrayList<Float> mOriginLeftS;
    //    int mOriginTop;
//    int mOriginHeight;
//    int mOriginWidth;
    private float mOriginCenterX;
    private float mOriginCenterY;
    private float mTargetHeight;
    private float mTargetWidth;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;
    private int defaultResId;
    private int mPosition;
    private List<View> photoViewList = new ArrayList<View>();
    private List<ViewParameter> viewParameters = new ArrayList<ViewParameter>();
    private BaseViewpagerAdapter<View> dragPhotoViewViewpagerAdapter;
    private ArrayList<Float> mOriginTops;
    private ArrayList<Float> mOriginHeighs;
    private ArrayList<Float> mOriginWidths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drag_photo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        mViewPager = (ViewPager) findViewById(R.id.photo_viewpager);

        mList = getIntent().getStringArrayListExtra(IMAGE_PATH);
        mPosition = getIntent().getIntExtra(POSITION, 0);
        defaultResId = getIntent().getIntExtra(DEFAULT_RESID, 0);

//        mPhotoViews = new DragPhotoView[mList.size()];

        for (int i = 0; i < mList.size(); i++) {
            DragPhotoView dragPhotoView = new DragPhotoView(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dragPhotoView.setLayoutParams(layoutParams);
            ImageLoader.getInstance().displayImage(mList.get(i), dragPhotoView, ImageUtil.getDisplayImageOptions(defaultResId));
            dragPhotoView.setOnTapListener(new DragPhotoView.OnTapListener() {
                @Override
                public void onTap(DragPhotoView view) {
                    finishWithAnimation(view);
                }
            });
            dragPhotoView.setOnExitListener(new DragPhotoView.OnExitListener() {
                @Override
                public void onExit(DragPhotoView view, float x, float y, float w, float h) {
                    performExitAnimation(view, x, y, w, h);
                }
            });
            photoViewList.add(dragPhotoView);
        }
        dragPhotoViewViewpagerAdapter = new BaseViewpagerAdapter<>();
        dragPhotoViewViewpagerAdapter.setList(photoViewList);
        mViewPager.setAdapter(dragPhotoViewViewpagerAdapter);
        mViewPager.setCurrentItem(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mOriginLeftS = (ArrayList<Float>) getIntent().getSerializableExtra("left");
                        mOriginTops = (ArrayList<Float>) getIntent().getSerializableExtra("top");
                        mOriginHeighs = (ArrayList<Float>) getIntent().getSerializableExtra("height");
                        mOriginWidths = (ArrayList<Float>) getIntent().getSerializableExtra("width");
//                        mOriginLeft = getIntent().getIntExtra("left", 0);
//                        mOriginTop = getIntent().getIntExtra("top", 0);
//                        mOriginHeight = getIntent().getIntExtra("height", 0);
//                        mOriginWidth = getIntent().getIntExtra("width", 0);
//                        mOriginCenterX = mOriginLeft + mOriginWidth / 2;
//                        mOriginCenterY = mOriginTop + mOriginHeight / 2;

                        for (int i = 0; i < photoViewList.size(); i++) {
                            int[] location = new int[2];
                            final DragPhotoView photoView = (DragPhotoView) photoViewList.get(i);
                            photoView.getLocationOnScreen(location);
                            mOriginCenterX = mOriginLeftS.get(i) + mOriginWidths.get(i);
                            mOriginCenterY = mOriginTops.get(i) + mOriginHeighs.get(i);

                            photoView.getLocationOnScreen(location);

                            mTargetHeight = (float) photoView.getHeight();
                            mTargetWidth = (float) photoView.getWidth();

                            mScaleX = (float) mOriginWidths.get(i) / mTargetWidth;
                            mScaleY = (float) mOriginHeighs.get(i) / mTargetHeight;

                            float targetCenterX = location[0] + mTargetWidth / 2;
                            float targetCenterY = location[1] + mTargetHeight / 2;

                            mTranslationX = mOriginCenterX - targetCenterX;
                            mTranslationY = mOriginCenterY - targetCenterY;

//                            photoView.setTranslationX(0);
//                            photoView.setTranslationY(0);

//                            photoView.setScaleX(1);
//                            photoView.setScaleY(1);
//
//                            performEnterAnimation();
//                            photoView.setMinScale(mScaleX);

                            ViewParameter viewParameter = new ViewParameter();
                            viewParameter.setTransX(mTranslationX);
                            viewParameter.setTransY(mTranslationY);

                            viewParameter.setScaleX(mScaleX);
                            viewParameter.setScaleY(mScaleY);
                            viewParameter.setCenterX(mOriginCenterX);
                            viewParameter.setCenterY(mOriginCenterY);

                            viewParameters.add(viewParameter);

                        }


//                        int[] location = new int[2];


//                        final DragPhotoView photoView = mPhotoViews[position];
                        final DragPhotoView photoView = (DragPhotoView) photoViewList.get(mPosition);
//                        photoView.getLocationOnScreen(location);

//                        mTargetHeight = (float) photoView.getHeight();
//                        mTargetWidth = (float) photoView.getWidth();
//                        mScaleX = (float) mOriginWidth / mTargetWidth;
//                        mScaleY = (float) mOriginHeight / mTargetHeight;

//                        float targetCenterX = location[0] + mTargetWidth / 2;
//                        float targetCenterY = location[1] + mTargetHeight / 2;

//                        mTranslationX = mOriginCenterX - targetCenterX;
//                        mTranslationY = mOriginCenterY - targetCenterY;


                        photoView.setTranslationX(viewParameters.get(mPosition).getTransX());
                        photoView.setTranslationY(viewParameters.get(mPosition).getTransY());

                        photoView.setScaleX(viewParameters.get(mPosition).getScaleX());
                        photoView.setScaleY(viewParameters.get(mPosition).getScaleY());

                        performEnterAnimation();
                    }
                });
    }

    /**
     * ===================================================================================
     * <p>
     * 底下是低版本"共享元素"实现   不需要过分关心  如有需要 可作为参考.
     * <p>
     * Code  under is shared transitions in all android versions implementation
     */
    private void performExitAnimation(final DragPhotoView view, float x, float y, float w, float h) {
        view.finishAnimationCallBack();
        float viewX = mTargetWidth / 2 + x - mTargetWidth * mScaleX / 2;
        float viewY = mTargetHeight / 2 + y - mTargetHeight * mScaleY / 2;
        view.setX(viewX);
        view.setY(viewY);

        float centerX = view.getX() + mOriginWidths.get(mPosition) / 2;
        float centerY = view.getY() + mOriginWidths.get(mPosition) / 2;

        float translateX = mOriginCenterX - centerX;
        float translateY = mOriginCenterY - centerY;


        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(view.getX(), view.getX() + translateX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(view.getY(), view.getY() + translateY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();
    }

    private void finishWithAnimation(final DragPhotoView photoView) {

        ValueAnimator alphaAnimator = ValueAnimator.ofInt(255, 0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setBackGroundAlpha((int) valueAnimator.getAnimatedValue());

            }
        });
        alphaAnimator.setDuration(300);
        alphaAnimator.start();


        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0,viewParameters.get(mPosition).getTransX());
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setX((Float) valueAnimator.getAnimatedValue());

            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0,viewParameters.get(mPosition).getTransY());
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, viewParameters.get(mPosition).getScaleY());
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, viewParameters.get(mPosition).getScaleX());
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    private void performEnterAnimation() {
        final DragPhotoView photoView = (DragPhotoView) photoViewList.get(mPosition);


        ValueAnimator alphaAnimator = ValueAnimator.ofInt(0, 255);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setBackGroundAlpha((int) valueAnimator.getAnimatedValue());

            }
        });
        alphaAnimator.setDuration(300);
        alphaAnimator.start();


        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(photoView.getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });

        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(photoView.getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(viewParameters.get(mPosition).getScaleY(), 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(viewParameters.get(mPosition).getScaleX(), 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation((DragPhotoView) photoViewList.get(mPosition));
    }

    class ViewParameter {
        private float transX;
        private float transY;
        private float scaleX;
        private float scaleY;
        private float centerX;
        private float centerY;

        public float getCenterY() {
            return centerY;
        }

        public void setCenterY(float centerY) {
            this.centerY = centerY;
        }

        public float getCenterX() {
            return centerX;
        }

        public void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        public float getTransX() {
            return transX;
        }

        public void setTransX(float transX) {
            this.transX = transX;
        }

        public float getTransY() {
            return transY;
        }

        public void setTransY(float transY) {
            this.transY = transY;
        }

        public float getScaleX() {
            return scaleX;
        }

        public void setScaleX(float scaleX) {
            this.scaleX = scaleX;
        }

        public float getScaleY() {
            return scaleY;
        }

        public void setScaleY(float scaleY) {
            this.scaleY = scaleY;
        }
    }
}
