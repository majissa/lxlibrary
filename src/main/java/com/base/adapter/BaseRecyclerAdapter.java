package com.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.base.R;
import com.base.listener.ItemClickListener;
import com.base.util.ScreenUtil;
import com.base.view.BaseLoadMoreFooter;

import java.util.ArrayList;
import java.util.List;


/**
 * 创建人：郑晓辉
 * 创建日期：2016/7/5
 * 描述：默认有头部和尾部，尾部默认有加载更多，只是隐藏了
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context context;
    protected List<T> list = new ArrayList<>();
    protected View.OnClickListener onClickListener;
    protected ItemClickListener itemClickListener;
    protected FrameLayout headerFrameLayout;//头部
    protected FrameLayout footerFrameLayout;//尾部
    private BaseLoadMoreFooter baseLoadMoreFooter;//加载更多,定义抽象类，传子类进来
    private OnLoadMoreListener onLoadMoreListener;
    private View emptyView;

    public View inflateHeaderView(@LayoutRes int resource) {
        View headView = LayoutInflater.from(context).inflate(resource, headerFrameLayout, false);
        headerFrameLayout.addView(headView);
        return headView;
    }

    public void setLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        headerFrameLayout = new FrameLayout(context);
        headerFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View lineView = new View(context);
        lineView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        lineView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        headerFrameLayout.addView(lineView);
        footerFrameLayout = new FrameLayout(context);
        footerFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setLoadMoreFooter(BaseLoadMoreFooter baseLoadMoreFooter) {
        if (baseLoadMoreFooter != null) {
            this.baseLoadMoreFooter = baseLoadMoreFooter;
            footerFrameLayout.addView(baseLoadMoreFooter);
        }
    }

    /**
     * 设置空内容的布局，最外层布局的高度设置成match_parent或者具体的dp值都可以。
     *
     * @param resource
     */
    public View inflaterEmptyView(@LayoutRes int resource) {
        this.emptyView = LayoutInflater.from(context).inflate(resource, footerFrameLayout, false);
//        emptyView.setVisibility(View.GONE);
        footerFrameLayout.addView(emptyView);
        return emptyView;
    }

    public View setFootView(@LayoutRes int resource) {
        return LayoutInflater.from(context).inflate(resource, footerFrameLayout, true);
    }

    public View setFootViewWithHeight(@LayoutRes int resource, int height) {
        View footView = LayoutInflater.from(context).inflate(resource, footerFrameLayout, false);
        footView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.getScreenWidth(context), height));
        footerFrameLayout.addView(footView);
        return footView;

    }

    /**
     * 设置空内容的布局和高度
     *
     * @param resource
     * @param height   空内容的布局高度
     */
    public View inflaterEmptyViewWithHeight(@LayoutRes int resource, int height) {
        this.emptyView = LayoutInflater.from(context).inflate(resource, footerFrameLayout, false);
        emptyView.setVisibility(View.GONE);
        //空内容布局的宽度为屏幕的宽度，高度为屏幕的高度-系统状态栏的高度-subtractHeight（通用头部的高度或者头部+尾部的高度）
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.getScreenWidth(context), height));
        footerFrameLayout.addView(emptyView);
        return emptyView;
    }

    /**
     * 设置空内容的布局和减掉其他控件所占的高
     *
     * @param resource
     * @param subtractHeight 项目通用头部高度，项目通用底部菜单等高度。
     */
    public View inflaterEmptyViewSubtractHeight(@LayoutRes int resource, int subtractHeight) {
        this.emptyView = LayoutInflater.from(context).inflate(resource, footerFrameLayout, false);
        emptyView.setVisibility(View.GONE);
        //空内容布局的宽度为屏幕的宽度，高度为屏幕的高度-系统状态栏的高度-subtractHeight（通用头部的高度或者头部+尾部的高度）
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.getScreenWidth(context),
                ScreenUtil.getScreenHeight(context) - ScreenUtil.getStatusBarHeight(context) - subtractHeight));
        footerFrameLayout.addView(emptyView);
        return emptyView;
    }

    public void setEmpty(boolean isEmpty) {
        if (emptyView != null) {
            if (isEmpty) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    protected View inflate(@LayoutRes int resource) {
        return LayoutInflater.from(context).inflate(resource, null, false);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    /**
     * 已经处理了添加头部和尾部的情况，position不用+1或-1处理，只要按正常的没有头部尾部的处理就行
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return list.size() + 2;//默认有头部和尾部
    }

    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemViewType.HEADER;
        } else if (position == getItemCount() - 1) {
            return ItemViewType.FOOTER;
        }
        return ItemViewType.ITEM;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
//                    return getItemViewType(position) == ItemViewType.HEADER//头部占几列
//                            ? gridManager.getSpanCount() : 1;
                    if (getItemViewType(position) == ItemViewType.FOOTER || getItemViewType(position) == ItemViewType.HEADER) {
                        return gridManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null
                && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            p.setFullSpan(true);//头部占满列
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.HEADER:
                return new HeadViewHolder(headerFrameLayout);
            case ItemViewType.ITEM:
                return onCreateItemViewHolder(parent);
            case ItemViewType.FOOTER:
                return new FooterViewHolder(footerFrameLayout);
            default:
                return null;
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent);


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ItemViewType.ITEM) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            position = position - 1;//减掉头部
            if (onLoadMoreListener != null && position == list.size() - 1) {//已经到达列表的底部,用于判断加载更多
                onLoadMoreListener.onLoadMore();
            }
            itemViewHolder.setListener(position);
            itemViewHolder.setItemClick(position);
            T t = list.get(position);
            if (t != null) {
                itemViewHolder.initValue(position, t);
            }
        }

    }

    public class HeadViewHolder extends RecyclerView.ViewHolder {

        public HeadViewHolder(View view) {
            super(view);
        }
    }

    public abstract class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View view) {
            super(view);
        }

        protected abstract void setListener(int position);

        protected abstract void initValue(int position, T t);

        protected void setItemClick(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, position);

                    }
                }
            });
        }

    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }
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
            if (emptyView != null) {
                if (setList.size() > 0) {
                    emptyView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (emptyView != null)
                emptyView.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    /**
     * 移除单个项
     *
     * @param position
     */
    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position + 1);//默认头部占1个位置要加上
        if (position != list.size()) {// 这个判断的意义就是如果移除的是最后一个，就不用管它了
            notifyItemRangeChanged(position + 1, list.size() - position);
        }
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
     * 清除数据并刷新
     */
    public void clear() {
        list.clear();
        setFooterNomal();
        notifyDataSetChanged();
    }

    /**
     * 设置Footer正常不可见状态
     */
    public void setFooterNomal() {
        if (baseLoadMoreFooter != null) {
            baseLoadMoreFooter.setScrollToButtomState(BaseLoadMoreFooter.STATE_NORMAL);
        }
    }

    /**
     * 设置Footer正在加载状态
     */
    public void setFooterLoading() {
        if (baseLoadMoreFooter != null) {
            baseLoadMoreFooter.setScrollToButtomState(BaseLoadMoreFooter.STATE_LOADING);
        }
    }

    /**
     * 设置Footer已显示全部内容状态
     */
    public void setFooterShowAll() {
        if (baseLoadMoreFooter != null) {
            baseLoadMoreFooter.setScrollToButtomState(BaseLoadMoreFooter.STATE_SHOW_ALL);
        }
    }

    private class ItemViewType {
        public static final int HEADER = 0;//头部
        public static final int ITEM = 1;//item项
        public static final int FOOTER = 2;//尾部
    }
}
