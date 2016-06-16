package com.example.l.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.l.common.R;
import com.example.l.common.constants.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by l on 2016/3/9.
 * 自定义View，根据不同的状态显示
 */
public class LoadingStateView extends FrameLayout {

    @Bind(R.id.view_state_empty)
    TextView iv_empty;

    @Bind(R.id.view_state_error)
    LinearLayout ll_error;

    @Bind(R.id.view_state_loading)
    LinearLayout pb_loading;

    @Bind(R.id.view_state_success)
    FrameLayout mFl_content;

    private int state = Constants.STATE_SUCCESS;

    public LoadingStateView(Context context) {
        this(context, null);
    }

    public LoadingStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_loading_state, this, true);
        ButterKnife.bind(this);
        updateState(state);
    }

    public void addSuccessView(View successView){
        if(successView != null && successView.getParent() == null){
            mFl_content.addView(successView);
        }else{
            throw new IllegalArgumentException("successView can't be null or have a parent view");
        }
    }


    /**
     * 根据不同的状态值，更新当前的View
     * @param currentState
     */
    public void updateState(int currentState) {
        switch (currentState) {
            case Constants.STATE_SUCCESS:
                mFl_content.setVisibility(View.VISIBLE);
                iv_empty.setVisibility(View.GONE);
                ll_error.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);
                break;
            case Constants.STATE_ERROR:
                mFl_content.setVisibility(View.GONE);
                iv_empty.setVisibility(View.GONE);
                ll_error.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                break;
            case Constants.STATE_LOADING:
                mFl_content.setVisibility(View.GONE);
                iv_empty.setVisibility(View.GONE);
                ll_error.setVisibility(View.GONE);
                pb_loading.setVisibility(View.VISIBLE);
                break;
            case Constants.STATE_EMPTY:
                mFl_content.setVisibility(View.GONE);
                iv_empty.setVisibility(View.VISIBLE);
                ll_error.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);
                break;
        }
    }

}
