package com.thirdparty.proxy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.thirdparty.proxy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:dongpo 创建时间: 6/30/2016
 * 描述:
 * 修改:
 */
public class NumberPickerFrameLayout extends FrameLayout{

    private LinearLayout mPickers;
    private PickerView mPicker1;
    private PickerView mPicker2;
    private LinearLayout mCover;

    public NumberPickerFrameLayout(Context context) {
        this(context,null);
    }

    public NumberPickerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_time_picker,this,true);
        mPickers = (LinearLayout) getChildAt(0);
        mPicker1 = (PickerView) mPickers.getChildAt(0);
        mPicker2 = (PickerView) mPickers.getChildAt(1);
        mCover = (LinearLayout) getChildAt(1);

        mPicker1.addOnChooseListener(new PickerView.OnChooseListener() {
            @Override
            public void onItemChoose(List<String> data, int position) {
                Log.d("Log_text", "NumberPickerFrameLayout+onItemChoose 选择的数据为"+ data.get(position));
                String str = data.get(position);
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                    list.add(str+"的子数据");
                }
                mPicker2.setData(list);
            }
        });

        mPicker2.addOnChooseListener(new PickerView.OnChooseListener() {
            @Override
            public void onItemChoose(List<String> data,int position) {
                Log.d("Log_text", "NumberPickerFrameLayout+onItemChoose 选择的数据为"+ data.get(position));
            }
        });

    }

    public void setData(List<String> datas) {
        mPicker1.setData(datas);
    }

    public void addOnChooseListener(PickerView.OnChooseListener listener){
        mPicker1.addOnChooseListener(listener);
    }
}
