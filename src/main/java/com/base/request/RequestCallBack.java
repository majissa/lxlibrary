package com.base.request;

/**
 * Created by 李响
 * 创建日期 2017/3/20
 * 描述：
 */
public interface RequestCallBack<T> {

    void onCompleted();

    void onError(String errorMsg);

    void onSuccess(T t);

    void onBefore();
}
