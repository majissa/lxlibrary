package com.base.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.base.R;
import com.base.common.BaseApplication;
import com.base.common.Const;
import com.base.common.GlobalBroadcastReceiver;
import com.base.common.WrapperSharedPreferences;
import com.base.imagedispose.PhotoPicker;
import com.base.interfaces.IGlobalReceiver;
import com.base.manager.ActivityStackManager;
import com.base.manager.FragmentStackManager;
import com.base.presenter.BasePresenter;
import com.base.presenter.BaseView;
import com.base.service.AppUpgradeService;
import com.base.util.AppUtil;
import com.base.util.ImageUtil;
import com.base.util.PopUtil;
import com.base.util.ScreenUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


/**
 * 公共基础父类activity，包含了HandleMessage消息队列处理，initView()，setListener()，initView()等抽象方法
 * 所有项目通用的功能放这里面，针对单个项目的功能放子类BaseActivity里。
 */
public abstract class AbstractBaseActivity<T extends BasePresenter> extends AppCompatActivity implements View.OnClickListener, IGlobalReceiver, BaseView {

    protected Context context;
    private GlobalBroadcastReceiver globalBroadcastReceiver;
    private long exitTime = 0;
    private int frameLayoutResId;//装载替换Fragment的FrameLayout布局Id
    private Fragment[] fragments;//多个Fragment，用于隐藏显示切换
    private int currentTab;//MyTabWidget登录之前的索引
    protected PhotoPicker photoPicker;//拍照和图库选择器
    public FragmentStackManager fragmentStackManager;//Fragment栈管理器
    protected ArrayList<String> multiImageList;
    protected int REQUEST_IMAGE = 110;
    protected T mPresenter;

    public int getCurrentTab() {
        return currentTab;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(inflateBaseLayoutId());
        init();
    }

    protected void init() {
        context = this;
        getMyApplication().getActivityStack().addActivity(this);
        initConfiguration();
        registerGlobleReceiver();//注册全局广播
        fragmentStackManager = new FragmentStackManager(this);
        initBaseView(getWindow().getDecorView());//初始化头部公共布局
        initView(getWindow().getDecorView());//查找控件
        setListener();//设置监听
        initPresenter(getPresenter());
        initValue();
    }

    //设置状态栏
    public void setStatusBar(Window window, @ColorRes int colorInt) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = ScreenUtil.getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundResource(colorInt);
        decorViewGroup.addView(statusBarView);
    }

    /**
     * 可配置的初始化，如果子类不想要其中的初始化配置，可以重写覆盖
     */
    protected void initConfiguration() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//全部竖屏，防止横竖屏切换
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//不自动弹出软键盘
    }

    /*默认为MVC模式，如果创建presenter的话，则是MVP模式,否則傳NULL*/
    protected void initPresenter(T basePresenter) {
        if (basePresenter == null) {
            new RuntimeException("presenter must be assemble!");
            return;
        }
        this.mPresenter = basePresenter;
        this.mPresenter.attachView(this);
        this.mPresenter.onCreate();
    }

    protected abstract int inflateBaseLayoutId();

    protected abstract int inflateMainLayoutId();

    protected abstract void initBaseView(View view);

    protected abstract void initView(View view);

    protected abstract void setListener();

    /**/
    protected abstract T getPresenter();

    protected abstract void initValue();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());//友盟统计
        MobclickAgent.onResume(this);//友盟统计
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName()); //友盟统计//保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);//友盟统计
    }

    protected void initPhotoPicker() {
        photoPicker = new PhotoPicker(this, new PhotoPicker.OnPhotoPickedListener() {
            @Override
            public void onPhotoPicked(String imagePath) {
                onPhotoPickerReturn(imagePath);
            }
        });
    }

    protected View inflate(@LayoutRes int resource) {
        return LayoutInflater.from(context).inflate(resource, null);
    }

//    /**
//     * 设置状态栏的颜色，xml布局中必须有
//     * android:fitsSystemWindows="true"
//     * android:clipToPadding="true"
//     * 这样两个属性才行，不然布局会跑到状态栏里。
//     * 如果是整个app都改变状态栏颜色，那就在BaseActivity中调用，
//     * 在activity_base公共布局中设置就可以了。
//     *
//     * @param id
//     */
//    protected void setStatusBarColor(@ColorRes int id) {
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
//        View statusBarView = new View(window.getContext());
//        int statusBarHeight = ScreenUtil.getStatusBarHeight(window.getContext());
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
//        params.gravity = Gravity.TOP;
//        statusBarView.setLayoutParams(params);
//        statusBarView.setBackgroundColor(getResources().getColor(id));
//        decorViewGroup.addView(statusBarView);
//    }

    /**
     * 隐藏状态栏，注意，可能会导致布局错位
     */
    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    public void jumpToLogin() {
        startActivity(new Intent(context, getMyApplication().getLoginClass()));
    }

    /**
     * 判断是否已经登录
     */
    public boolean isLoggedIn() {
        return getMyApplication().isLoggedIn();
    }

    /**
     * 跳转到BaseWebViewActivity的子类
     *
     * @param url 网页地址
     */
    public void jumpToWebViewActivity(String url) {
        jumpToWebViewActivity(url, null);
    }

    /**
     * 跳转到BaseWebViewActivity的子类
     *
     * @param url    网页地址
     * @param bundle 可以传任意数据类型
     */
    public void jumpToWebViewActivity(String url, Bundle bundle) {
        if (getMyApplication().getWebViewClass() != null) {
            Intent intent = new Intent(this, getMyApplication().getWebViewClass());
            intent.putExtra(BaseWebViewActivity.URL, url);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        }
    }

    /**
     * 跳转PhotoViewActivity，加载多张网络或本地图片
     *
     * @param imagePath         图片地址集合,网络和本地的都可以
     * @param defaultDrawableId 默认显示的本地图片id
     */
    public void jumpToPhotoViewActivity(String imagePath, int defaultDrawableId) {
        ArrayList<String> list = new ArrayList<>();
        list.add(imagePath);
        jumpToPhotoViewActivity(list, defaultDrawableId, 0);
    }

    /**
     * 跳转PhotoViewActivity，加载多张网络或本地图片
     *
     * @param imagePathList     图片地址集合,网络和本地的都可以
     * @param defaultDrawableId 默认显示的本地图片id
     * @param currentPosition   默认显示第几张图
     */
    public void jumpToPhotoViewActivity(ArrayList<String> imagePathList, int defaultDrawableId, int currentPosition) {
        Intent intent = new Intent(this, PhotoViewActivity.class);
        intent.putStringArrayListExtra(PhotoViewActivity.IMAGE_PATH_LIST, imagePathList);
        intent.putExtra(PhotoViewActivity.DEFAULT_DRAWABLE_ID, defaultDrawableId);
        intent.putExtra(PhotoViewActivity.CURRENT_POSITION, currentPosition);
        startActivity(intent);
    }

    /**
     * 跳转到PhotoViewActivity，加载单张本地资源图片
     *
     * @param anchorImageView
     */
    public void jumpToPhotoViewActivity(ImageView anchorImageView) {
        Intent intent = new Intent(this, PhotoViewActivity.class);
        intent.putExtra(PhotoViewActivity.BITMAP, ImageUtil.drawableToBitmap(anchorImageView.getDrawable()));
        startActivity(intent);
    }

    /**
     * 开启apk升级服务
     *
     * @param downloadUrl
     */
    public void startAppUpgradeService(String downloadUrl) {
        Intent intent = new Intent(context, AppUpgradeService.class);
        intent.putExtra(AppUpgradeService.DOWNLOAD_URL, downloadUrl);
        context.startService(intent);
    }

    /**
     * 是不是第一次安装后执行，覆盖安装或版本升级不算（因为share数据会保留）
     *
     * @param key SharedPreferences的key,不同的key针对不同的事件，是1对多的关系，多个事件互不影响
     *            注意：每次调用该方法的地方key都应该不同，因为每次执行完都可能改变了share的值
     * @return
     */
    public boolean isFirstTimeSetupExecute(String key) {
        if (WrapperSharedPreferences.getInstance(this).getBoolean(key, true)) {
            WrapperSharedPreferences.getInstance(this).putBoolean(key, false);
            return true;
        }
        return false;
    }

    /**
     * 是不是新版本升级或者第一次安装（第一次share没值，默认为0）
     *
     * @param key SharedPreferences的key,不同的key针对不同的事件，是1对多的关系，多个事件互不影响
     *            注意：每次调用该方法的地方key都应该不同，因为每次执行完都可能改变了share的值
     * @return
     */
    public boolean isNewVersion(String key) {
        int oldVersionCode = WrapperSharedPreferences.getInstance(this).getInt(key, 0);
        int newVersionCode = AppUtil.getVersionCode(this);
        if (oldVersionCode != newVersionCode) {
            WrapperSharedPreferences.getInstance(this).putInt(key, newVersionCode);
            return true;
        }
        return false;
    }

    /**
     * 给TextView设置下划线
     *
     * @param textView
     */
    public void setTextViewUnderLine(TextView textView) {
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        textView.getPaint().setAntiAlias(true);//抗锯齿
    }


    /**
     * 初始化多个Fragment，用于隐藏显示切换,并add第一个Fragment到Activity
     *
     * @param frameLayoutResId 装载替换Fragment的FrameLayout布局Id
     * @param fragments        主页等页面多个Fragment切换的Fragment数组
     */
    protected void initMultiFragment(int frameLayoutResId, Fragment[] fragments) {
        this.frameLayoutResId = frameLayoutResId;
        this.fragments = fragments;
        getFragmentManager().beginTransaction().add(frameLayoutResId, fragments[0], "0").commitAllowingStateLoss();
    }

    public abstract BaseApplication getMyApplication();

    /**
     * 多个Fragment切换显示的索引
     *
     * @param position
     */
    public void switchFragment(int position) {
        if (fragments == null) {
            return;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i != position && fragments[i] != null && fragments[i].isAdded()) {
                transaction.hide(fragments[i]);
            }
        }
        if (!fragments[position].isAdded()) {
            transaction.add(frameLayoutResId, fragments[position], String.valueOf(position)).commitAllowingStateLoss();//用commit有时候会报错
        } else {
            transaction.show(fragments[position]).commitAllowingStateLoss();
        }
        currentTab = position;
    }

    protected ActivityStackManager getActivityStackManager() {
        return getMyApplication().getActivityStack();
    }

    public WrapperSharedPreferences getZSharedPreferences() {
        return WrapperSharedPreferences.getInstance(this);
    }

    /**
     * 注册应用内广播接收器
     */
    public void registerGlobleReceiver() {
        globalBroadcastReceiver = new GlobalBroadcastReceiver(this);
        registerReceiver(globalBroadcastReceiver, globalBroadcastReceiver.getIntentFilter());
    }

    /**
     * 取消注册应用内广播接收器
     */
    public void unRegisterGlobleReceiver() {
        unregisterReceiver(globalBroadcastReceiver);
    }

  /*  @Override
    public void onBackPressed() {
        finish();
    }*/

    /**
     * 手动弹出软键盘
     */
    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public void hideSoftInput(View view) {
        if (view.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获取点击事件，我把它加入到base中,为了点击其他位置的时候收起软键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                hideSoftInput(view);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定是否需要隐藏
     *
     * @param v
     * @param ev
     * @return
     */
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 快速点击两次退出程序
     */
    protected void exitApp() {
        // 判断2次点击事件时间间隔
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            PopUtil.toast(this, R.string.exit_app_toast);
            exitTime = System.currentTimeMillis();
        } else {
            getMyApplication().getActivityStack().exitApp();
        }
    }

    /**
     * 程序退到桌面，模拟Home键事件
     */
    protected void fallBackHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int count = getMyApplication().getActivityStack().getActivityStartedCount();
        if (count == 0 && getMyApplication().getActivityStack().getOnAppDisplayListener() != null) {
            getMyApplication().getActivityStack().getOnAppDisplayListener().onFront();
        }
        count++;
        getMyApplication().getActivityStack().setActivityStartedCount(count);
    }

    @Override
    protected void onStop() {
        super.onStop();
        int count = getMyApplication().getActivityStack().getActivityStartedCount();
        if (count == 0 && getMyApplication().getActivityStack().getOnAppDisplayListener() != null) {
            getMyApplication().getActivityStack().getOnAppDisplayListener().onBack();
        }
        count--;
        getMyApplication().getActivityStack().setActivityStartedCount(count);
    }

    /**
     * 在登录情况下执行操作，如果未登录，则先登录再执行操作
     *
     * @param loginDoCode 登录操作码
     */
    protected void doWithLogin(String loginDoCode) {
        Const.LOGIN_DO_CODE = loginDoCode;
        if (isLoggedIn()) {
            afterLogin(loginDoCode);
        } else {
            startActivityForResult(new Intent(context, getMyApplication().getLoginClass()), Const.LOGIN_REQUEST_CODE);
        }
    }

    /**
     * 登录后执行的回调方法，留给子类覆盖实现
     *
     * @param loginDoCode 登录操作码
     */
    protected void afterLogin(String loginDoCode) {

    }

    /**
     * 取消登录
     *
     * @param loginDoCode
     */
    protected void cancelLogin(String loginDoCode) {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Const.LOGIN_REQUEST_CODE) {
            afterLogin(Const.LOGIN_DO_CODE);
        }
        if (resultCode == Activity.RESULT_CANCELED && requestCode == Const.LOGIN_REQUEST_CODE) {
            cancelLogin(Const.LOGIN_DO_CODE);
        }
        if (photoPicker != null) {
            photoPicker.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 获取到PhotoPicker返回的图片
     *
     * @param imagePath
     */
    protected void onPhotoPickerReturn(String imagePath) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMyApplication().getActivityStack().removeActivity(this);
        unRegisterGlobleReceiver();
//        OkGo.getInstance().cancelTag(this);//Activity销毁时，取消网络请求
        if (photoPicker != null) {
            photoPicker.onDestroy();
        }
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    /**
     * 发送通用广播带参数
     */
    public void sendCommonBroadcast(String identifyCode, Bundle bundle) {
        globalBroadcastReceiver.broadcastIdentifyCode = identifyCode;
        Intent intent = new Intent(GlobalBroadcastReceiver.ACTION_COMMON);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 发送通用广播
     */
    public void sendCommonBroadcast(String identifyCode) {
        globalBroadcastReceiver.broadcastIdentifyCode = identifyCode;
        sendBroadcast(new Intent(GlobalBroadcastReceiver.ACTION_COMMON));
    }

    public void onCommonBroadcastReceive(Intent intent, String identifyCode) {
    }


    /**
     * 发送登录广播
     */
    public void sendLoginBroadcast() {
        sendBroadcast(new Intent(GlobalBroadcastReceiver.ACTION_LOGIN));
    }

    /**
     * 发送注销广播
     */
    public void sendLogoutBroadcast() {
        sendBroadcast(new Intent(GlobalBroadcastReceiver.ACTION_LOGOUT));
    }

    public void onLogin(Intent intent) {

    }

    public void onLogout(Intent intent) {

    }

    public void onMessageReceived(String title, String message, String extras, String contentType) {

    }

    public void onNotificationReceived(String title, String content, String extras, String contentType) {

    }

    public void onNotificationOpened(String title, String content, String extras) {

    }

}
