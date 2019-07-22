package com.base.presenter;

/**
 * Created by Eric on 2017/1/16.
 */

public interface BaseView {

    void showProgress();

    void hideProgress();

    void showMsg(String message);

    void onError();

    void jumpLogin();

    String getToken();

}
