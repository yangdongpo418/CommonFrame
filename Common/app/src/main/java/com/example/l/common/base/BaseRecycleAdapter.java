package com.example.l.common.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author:dongpo 创建时间: 6/21/2016
 * 描述:
 * 修改:
 */
public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private SparseArray<List<? extends Object>> mDatas_layouts;
    private int itemTypeCount;


    public BaseRecycleAdapter(List<T> data, int layoutId) {
        mDatas_layouts = new SparseArray<>();
        mDatas_layouts.put(layoutId,data);
        itemTypeCount = 1;
    }

    public BaseRecycleAdapter() {
        mDatas_layouts = new SparseArray<>();
    }

    public void addItemType(List<? extends Object> data, int layoutId){
        mDatas_layouts.put(layoutId,data);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = getLayoutIdFromViewType(viewType);
        ViewHolder holder = ViewHolder.get(parent.getContext(), null, parent, layoutId, -1);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.updatePosition(position);
        int viewType = getItemViewType(position);
        setItemData(holder, position, viewType);
    }

    protected abstract void setItemData(ViewHolder holder, int position, int viewType);


    @Override
    public int getItemCount() {
        int totalCount = 0;

        for (int i = 0; i < mDatas_layouts.size(); i++) {
            int layoutId = mDatas_layouts.keyAt(i);
            List<?> data = mDatas_layouts.get(layoutId);
            totalCount += data.size();
        }

        return totalCount;
    }


    public int getItemTypeCount() {
        return itemTypeCount;
    }

    private List<? extends Object> getDataFromViewType(int viewType) {
        return mDatas_layouts.get(viewType);
    }

    private int getLayoutIdFromViewType(int viewType) {
        return viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return getHolderType(position);
    }

    protected int getHolderType(int position) {
        return mDatas_layouts.keyAt(0);
    }

    public <K> List<K> getData(int layoutId){
        return (List<K>) mDatas_layouts.get(layoutId);
    }

}
