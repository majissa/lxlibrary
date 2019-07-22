package com.base.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;

import com.base.activity.AbstractBaseActivity;
import com.base.common.Const;
import com.base.common.GlobalBroadcastReceiver;
import com.base.imagedispose.PhotoPicker;
import com.base.interfaces.IGlobalReceiver;
import com.base.presenter.BasePresenter;
import com.base.presenter.BaseView;
import com.umeng.analytics.MobclickAgent;


/**
 * 公共基础父类Fragment，initView(View view)，setListener()，initValue()
 * 抽象方法
 *
 * @author z
 */
public abstract class AbstractBaseFragment<T extends BasePresenter> extends Fragment implements BaseView, View.OnClickListener, IGlobalReceiver {

    protected Context context;
    protected AbstractBaseActivity abstractBaseActivity;
    private GlobalBroadcastReceiver globalBroadcastReceiver;
    protected PhotoPicker photoPicker;//拍照和图库选择器
    protected T mPresenter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    protected void init(View view) {
        context = getActivity();
        abstractBaseActivity = (AbstractBaseActivity) getActivity();
        registerGlobleReceiver();
        initBaseView(view);//初始化头部公共布局
        initView(view);//查找控件
        setListener();//设置监听
        initPresenter(getPresenter());//初始化数据
        initValue();
    }

    protected abstract int inflateBaseLayoutId();

    protected abstract int inflateMainLayoutId();

    protected abstract void initBaseView(View view);

    protected abstract void initView(View view);

    protected abstract void setListener();

    protected abstract void initValue();

    /**/
    protected abstract T getPresenter();


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName()); //友盟统计
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());//友盟统计
    }

    protected void initPhotoPicker() {
        photoPicker = new PhotoPicker(this, new PhotoPicker.OnPhotoPickedListener() {
            @Override
            public void onPhotoPicked(String imagePath) {
                onPhotoPickerReturn(imagePath);
            }
        });
    }

    ;

    protected View inflate(@LayoutRes int resource) {
        return LayoutInflater.from(context).inflate(resource, null);
    }

    /**
     * 注册应用内广播接收器
     */
    public void registerGlobleReceiver() {
        globalBroadcastReceiver = new GlobalBroadcastReceiver(this);
        getActivity().registerReceiver(globalBroadcastReceiver, globalBroadcastReceiver.getIntentFilter());
    }

    public void unRegisterGlobleReceiver() {
        getActivity().unregisterReceiver(globalBroadcastReceiver);
    }

    /**
     * 在登录情况下执行操作，如果未登录，则先登录再执行操作
     *
     * @param loginDoCode 登录操作码
     */
    protected void doWithLogin(String loginDoCode) {
        Const.LOGIN_DO_CODE = loginDoCode;
        if (abstractBaseActivity.isLoggedIn()) {
            afterLogin(loginDoCode);
        } else {
            startActivityForResult(new Intent(context, abstractBaseActivity.getMyApplication().getLoginClass()), Const.LOGIN_REQUEST_CODE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /*默认为MVC模式，如果创建presenter的话，则是MVP模式,否則傳NULL*/
    protected void initPresenter(T presenter) {
        if (presenter == null) {
            new RuntimeException("presenter must be assemble!");
            return;
        }
        this.mPresenter = presenter;
        this.mPresenter.attachView(this);
        this.mPresenter.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterGlobleReceiver();
        //Activity销毁时，取消网络请求
//		OkGo.getInstance().cancelTag(this);
        if (photoPicker != null) {
            photoPicker.onDestroy();
        }

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    /**
     * 发送通用广播
     */
    public void sendCommonBroadcast(String identifyCode) {
        abstractBaseActivity.sendCommonBroadcast(identifyCode);
    }

    public void onCommonBroadcastReceive(Intent intent, String identifyCode) {

    }

    /**
     * 发送登录广播
     */
    public void sendLoginBroadcast() {
        abstractBaseActivity.sendLoginBroadcast();
    }

    /**
     * 发送注销广播
     */
    public void sendLogoutBroadcast() {
        abstractBaseActivity.sendLogoutBroadcast();
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
