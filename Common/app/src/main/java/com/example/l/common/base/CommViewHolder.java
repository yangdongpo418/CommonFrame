package com.example.l.common.base;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author:dongpo
 * 创建时间: 2016/3/31
 * 描述:对BaseAdapter的getView方法进行封装
 * 作用: 1、对convertView进行复用，并将convertView进行返回
 *      2、将findViewById找到的View存入SparseArray中。
 * 修改:
 */
public class CommViewHolder {
    /**------------ListView中的ItemView----------------*/
    private final View mConvertView;
    /**------------用来缓存mConvertView中的子View对象，key为子view的id，value为子view的View对象----------------*/
    private final SparseArray<View> cache;

    private CommViewHolder(View convertView){
        mConvertView = convertView;
        cache=new SparseArray<>();
    }

    /**
     * @param layoutId convertView的布局id
     * @param convertView ListView复用的itemView
     * @param parent ListView本身
     * @return CommViewHolder
     * BaseAdapter的getView方法，每执行一次，本方法就执行一次，主要对ConvertView进行复用，和对ConvertView下的子View进行保存；
     */
    public static CommViewHolder getInstance(int layoutId,View convertView,ViewGroup parent){
        CommViewHolder holder=null;
        if(convertView==null){
            //如果convertView为null，进行打气，并将打气后的View对象，通过构造方法，保存到成员变量中
            convertView = View.inflate(parent.getContext(), layoutId, null);
            holder=new CommViewHolder(convertView);
            //将holder作为标签存到convertView中
            convertView.setTag(holder);
        }else{
            holder= (CommViewHolder) convertView.getTag();
        }
        return holder;
    }

    /**
     * @param viewId 传入每个子View的Id
     * @return 子View对应的View对象，如果SpareArray集合中没有则进行findViewById，有则直接进行返回
     */
    public View getViewById(int viewId){
        View child = cache.get(viewId);
        if (child == null) {
            child=mConvertView.findViewById(viewId);
            cache.put(viewId,child);
        }
        return child;
    }

    /**
     * @return 返回convertView
     */
    public View getRootView(){
        return mConvertView;
    }
}
