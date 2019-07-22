package com.base.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.base.adapter.BaseAddPictureAdapter;
import com.base.listener.ItemClickListener;
import com.base.util.RegexUtil;

import java.util.ArrayList;


/**
 * 创建人：郑晓辉
 * 创建日期：2016/7/8
 * 描述：添加图片的GridView
 */
public class AddPictureGridView extends FullGridView {

    private BaseAddPictureAdapter baseAddPictureAdapter;
    private boolean isCompress = true;//是否压缩，默认true压缩
    private int REQUEST_CODE = -1;
    private DeleteItemListener deleteItemListener;

    public AddPictureGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddPictureGridView(Context context) {
        super(context);
    }

    public AddPictureGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(final Activity activity, final BaseAddPictureAdapter baseAddPictureAdapter) {
        init(activity, baseAddPictureAdapter, true, REQUEST_CODE);
    }

    public BaseAddPictureAdapter getBaseAddPictureAdapter() {
        return baseAddPictureAdapter;
    }

    public void init(final Activity activity, final BaseAddPictureAdapter baseAddPictureAdapter, boolean isCompress, final int REQUEST_CODE) {
        this.REQUEST_CODE = REQUEST_CODE;
        this.baseAddPictureAdapter = baseAddPictureAdapter;
        this.isCompress = isCompress;
        baseAddPictureAdapter.setDeleteItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                baseAddPictureAdapter.getList().remove(position);
                baseAddPictureAdapter.notifyDataSetChanged();
            }
        });
        baseAddPictureAdapter.setAddItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (REQUEST_CODE != -1) {
//                    MultiImageSelector.create().multi().count(baseAddPictureAdapter.getMaxCount()).origin((ArrayList<String>) baseAddPictureAdapter.getList()).start(activity, REQUEST_CODE);
                } else {
//                    MultiImageSelector.create().multi().count(baseAddPictureAdapter.getMaxCount()).origin((ArrayList<String>) baseAddPictureAdapter.getList()).start(activity, MultiImageSelector.REQUEST_IMAGE_GRID);
                }

            }
        });
        setAdapter(baseAddPictureAdapter);
    }

    public void init(final Fragment fragment, final BaseAddPictureAdapter baseAddPictureAdapter, final int REQUEST_CODE, DeleteItemListener deleteItemListener) {
        init(fragment, baseAddPictureAdapter, true, REQUEST_CODE, deleteItemListener);
    }

    public void init(final Fragment fragment, final BaseAddPictureAdapter baseAddPictureAdapter, boolean isCompress, final int REQUEST_CODE, final DeleteItemListener deleteItemListener) {
        this.baseAddPictureAdapter = baseAddPictureAdapter;
        this.isCompress = isCompress;
        this.REQUEST_CODE = REQUEST_CODE;
        this.deleteItemListener = deleteItemListener;
        baseAddPictureAdapter.setDeleteItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String item = baseAddPictureAdapter.getItem(position);
                if (RegexUtil.checkURL(item)) {
                    if (deleteItemListener != null)
                        deleteItemListener.onItemClick(view, position);
                } else {
                    baseAddPictureAdapter.getList().remove(item);
                    baseAddPictureAdapter.notifyDataSetChanged();
                }

            }
        });
        baseAddPictureAdapter.setAddItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (REQUEST_CODE != -1) {
//                    MultiImageSelector.create().multi().count(baseAddPictureAdapter.getMaxCount()).origin((ArrayList<String>) baseAddPictureAdapter.getList()).start(fragment, REQUEST_CODE);
                } else {
//                    MultiImageSelector.create().multi().count(baseAddPictureAdapter.getMaxCount()).origin((ArrayList<String>) baseAddPictureAdapter.getList()).start(fragment, MultiImageSelector.REQUEST_IMAGE_GRID);
                }
            }
        });
        setAdapter(baseAddPictureAdapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == MultiImageSelector.REQUEST_IMAGE_GRID) {
//                List<String> pathList = new ArrayList<>();
//                if (isCompress) {
//                    for (int i = 0; i < data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT).size(); i++) {
//                        pathList.add(ImageUtil.compressBitmap(getContext(), data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT).get(i)));
//                    }
//                } else {
//                    pathList.addAll(data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT));
//                }
//                baseAddPictureAdapter.setList(pathList);
//            } else if (requestCode == REQUEST_CODE) {
//                List<String> pathList = new ArrayList<>();
//                if (isCompress) {
//                    for (int i = 0; i < data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT).size(); i++) {
//                        pathList.add(data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT).get(i));
////                        pathList.add(ImageUtil.compressBitmap(getContext(), data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT).get(i)));
//                    }
//                } else {
//                    pathList.addAll(data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT));
//                }
//                baseAddPictureAdapter.setList(pathList);
//                if (deleteItemListener != null)
//                    deleteItemListener.rebackListener(data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT));
//            }
//        }
    }

    public interface DeleteItemListener {
        void onItemClick(View view, int position);

        void rebackListener(ArrayList<String> imagePaths);
    }

}
