package com.base.adapter;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import com.base.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************************************
 * <p/>
 * 说明：通用列表适配器抽象基类(推荐使用ZBaseAdapterAdvance)
 * <p/>
 * 作者：李响
 * <p/>
 * 创建日期：2013-10-30
 * <p/>
 * 描述 :抽象类MyBaseAdapter继承了BaseAdapter，使用了泛型。 传入list,已经写了getCount()，
 * 子类不用再写。子类继承后只要重写getView()方法，并且增加ViewHolder就可以了。
 * <p/>
 * **********************************************************
 */
@Deprecated
public abstract class ZBaseAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> list = new ArrayList<T>();
    protected View.OnClickListener onClickListener;
    protected ItemClickListener itemClickListener;

    public ZBaseAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 添加数据，一般用于上拉加载更多，分页数据添加或者初始化数据
     *
     * @param addList
     */
    public void addList(List<T> addList) {
        if (addList != null && addList.size() > 0) {
            list.addAll(addList);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据，一般用于下拉刷新，清空旧数据并添加新数据
     *
     * @param setList
     */
    public void setList(List<T> setList) {
        list.clear();
        if (setList != null) {
            list = setList;
        }
        notifyDataSetChanged();
    }

    /**
     * 获取List
     *
     * @return
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 移除单个项
     *
     * @param position
     */
    public void removeItem(int position) {
        if (position < list.size()) {
            list.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * 清除数据并刷新
     */
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

}
