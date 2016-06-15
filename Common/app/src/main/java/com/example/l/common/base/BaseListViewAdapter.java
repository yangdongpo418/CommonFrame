package com.example.l.common.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author:dongpo
 * 创建时间: 2016/3/30
 * 描述:此类为ListView的Adapter的基类，只需要继承此类，实现setItemData方法
 *      通过CommViewHolder的getViewById方法，获得每一个View的对象，再进行设置数据即可；
 *
 */
public abstract class BaseListViewAdapter<T> extends BaseAdapter{
    public List<T> mContent;
    private int mLayoutId;

    public BaseListViewAdapter(List<T> content,int layoutId){
        mContent=content;
        mLayoutId = layoutId;
    }

    public void setData(List<T> content){
        mContent.clear();
        mContent.addAll(content);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if(mContent==null){
            return 0;
        }
        return mContent.size();
    }

    @Override
    public Object getItem(int position) {
        return mContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommViewHolder holder = CommViewHolder.getInstance(mLayoutId, convertView, parent);
        //不知道子类具体的设置方式，进行抽象，由子类实现
        setItemData(holder,position);
        return holder.getRootView();

    }

    /**
     * @param holder 通过holder的getViewById方法获得子View的View对象
     * @param position 当前ListView对应的索引
     */
    public abstract void setItemData(CommViewHolder holder,int position);

}

