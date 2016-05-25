package com.example.l.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author:dongpo 创建时间: 2016/4/23
 * 描述:
 * 修改:
 */
public class ContentLinearLayout extends LinearLayout {

    private boolean isIntercepted=false;

    public ContentLinearLayout(Context context) {
        super(context);
    }

    public ContentLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return isIntercepted;
    }

    public void setIntercepted(boolean isIntercepted){
        this.isIntercepted=isIntercepted;
    }
}
