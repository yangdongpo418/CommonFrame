package com.thirdparty.proxy.map.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.amap.api.maps2d.MapView;

/**
 * @author:dongpo 创建时间: 6/8/2016
 * 描述:
 * 修改:
 */
public class TouchEventMapView extends MapView {
    public TouchEventMapView(Context context) {
        super(context);
    }

    public TouchEventMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
