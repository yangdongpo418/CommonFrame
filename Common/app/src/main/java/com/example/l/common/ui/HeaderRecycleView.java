package com.example.l.common.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author:dongpo 创建时间: 6/22/2016
 * 描述:
 * 修改:
 */
public class HeaderRecycleView extends RecyclerView {
    private View mHeader;
    private View mFooter;

    public HeaderRecycleView(Context context) {
        super(context);
    }

    public HeaderRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void addHeader(View header){
        mHeader = header;
        addView(mHeader,0);
    }

    public void addFooter(View footer){
        mFooter = footer;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mHeader.getLayoutParams() == null){
            mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if(mFooter.getLayoutParams() == null){
            mFooter.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        
        measureChild(mHeader,widthMeasureSpec,heightMeasureSpec);
        measureChild(mFooter,widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int headerWidth = mHeader.getMeasuredWidth();
        int headerHeight = mHeader.getMeasuredHeight();
        
        int footerWidth = mFooter.getMeasuredWidth();
        int footerHeight = mFooter.getMeasuredHeight();
        
        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredHeight();

        //getLayoutManager().addView();
        
        mHeader.layout(l,t,l+headerWidth,t+headerHeight);
        //mFooter.layout();
        super.onLayout(changed,headerWidth, t, r, b);
    }
}
