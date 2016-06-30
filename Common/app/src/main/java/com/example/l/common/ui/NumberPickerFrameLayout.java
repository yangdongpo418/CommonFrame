package com.example.l.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.l.common.R;

import java.util.List;

/**
 * @author:dongpo 创建时间: 6/30/2016
 * 描述:
 * 修改:
 */
public class NumberPickerFrameLayout extends FrameLayout{

    private NumberTimePicker mPicker;
    private LinearLayout mCover;
    private List<String> mDatas;

    public NumberPickerFrameLayout(Context context) {
        this(context,null);
    }

    public NumberPickerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_time_picker,this,true);
        mPicker = (NumberTimePicker) getChildAt(0);
        mCover = (LinearLayout) getChildAt(1);

        mPicker.addOnChooseListener(new NumberTimePicker.OnChooseListener() {
            @Override
            public void onItemChoose(int position) {
                Log.d("Log_text", "NumberPickerFrameLayout+onItemChoose 选择的数据为"+ mDatas.get(position));
            }
        });
    }

    public void setData(List<String> datas) {
        mPicker.setData(datas);
        mDatas = datas;
    }

}
