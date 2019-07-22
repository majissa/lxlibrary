package com.base.presenter;

import androidx.annotation.NonNull;

public interface BasePresenter {

    //    void onResume();

    void onCreate();

    void attachView(@NonNull BaseView view);

    void onDestroy();

}
