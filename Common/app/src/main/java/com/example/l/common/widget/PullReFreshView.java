package com.example.l.common.widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.l.common.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author:dongpo 创建时间: 5/30/2016
 * 描述:
 * 修改:
 */
public class PullReFreshView extends ViewGroup {

    private ViewDragHelper mViewDragHelper;
    private View mContent;
    private ViewGroup mPullView;

    private int mParentHeight;
    private int mParentWidth;
    private int mContentWidth;
    private int mContentHeight;

    public static final int STATE_CLOSE = 1;
    public static final int STATE_OPEN = 2;
    public static final int STATE_REFRESH = 3;
    private int state = STATE_CLOSE;
    private int mPullHeight;
    private int mRange;
    private int mMiddleLine;
    private int mPullWidth;
    private boolean isRevised = false;

    @Bind(R.id.view_tv_pull_refresh)
    TextView mTvRefresh;

    public PullReFreshView(Context context, View contentView) {
        super(context, null);

    }

    public PullReFreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPullView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_pull_refresh, this, true);
        ButterKnife.bind(this);

        mViewDragHelper = ViewDragHelper.create(this, new ViewDragCallBack());
    }

    public class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == mContent) {
                return true;
            }
            return false;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 0;
        }


        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == mContent) {
                int top = releasedChild.getTop();
                if (top >= mPullHeight && (state == STATE_OPEN || state == STATE_REFRESH)) {
                    open();
                } else if (top < mPullHeight && state == STATE_CLOSE) {
                    close();
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mContent) {
                mPullView.offsetTopAndBottom(dy);
                int mContentTop = mContent.getTop();
                if (dy < 0 && mContentTop == mPullHeight && state == STATE_OPEN) {
                    Log.d("Log_text", "ViewDragCallBack+onViewPositionChanged" + STATE_OPEN);
                    state = STATE_CLOSE;
                    onPullBackUIChange();
                } else if (dy > 0 && mContentTop == mPullHeight && state == STATE_CLOSE) {
                    Log.d("Log_text", "ViewDragCallBack+onViewPositionChanged" + STATE_OPEN);
                    state = STATE_OPEN;
                    onPullOverUIChange();
                } else {
                    invalidate();
                }

            }

        }
    }

    private void open() {
        if (mViewDragHelper.smoothSlideViewTo(mPullView, 0, 0) && mViewDragHelper.smoothSlideViewTo(mContent, 0, mPullHeight)) {
            invalidate();
        }

        if (state == STATE_OPEN) {
            state = STATE_REFRESH;
            onRrefeshUIChange();
        } else if (state == STATE_REFRESH) {
            //正在刷新 不进行重复刷新 编写代码请慎重
        }

    }

    public void close() {
        if (mViewDragHelper.smoothSlideViewTo(mPullView, 0, -mPullHeight) && mViewDragHelper.smoothSlideViewTo(mContent, 0, 0)) {
            invalidate();
        }
        state = STATE_CLOSE;
        onPullBackUIChange();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    public void setContentView(View contentView) {
        mContent = contentView;
        addView(contentView, 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mParentWidth = MeasureSpec.getSize(widthMeasureSpec);
        mParentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = child.getLayoutParams();

            int childWidthMode = MeasureSpec.UNSPECIFIED;
            int childHeightMode = MeasureSpec.UNSPECIFIED;

            int childWidth = 0;
            int childHeight = 0;
            if (layoutParams.height > 0) {
                childHeightMode = MeasureSpec.EXACTLY;
                childHeight = layoutParams.height;
            } else if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
                childHeightMode = MeasureSpec.AT_MOST;
                childHeight = mParentHeight;
            } else if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                childHeightMode = MeasureSpec.EXACTLY;
                childHeight = mParentHeight;
            } else {
                throw new IllegalArgumentException("子view的大小未知");
            }


            if (layoutParams.width > 0) {
                childWidthMode = MeasureSpec.EXACTLY;
                childWidth = layoutParams.width;
            } else if (layoutParams.width == LayoutParams.WRAP_CONTENT) {
                childWidthMode = MeasureSpec.AT_MOST;
                childWidth = mParentWidth;
            } else if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                childWidthMode = MeasureSpec.EXACTLY;
                childWidth = mParentWidth;
            } else {
                throw new IllegalArgumentException("子view的大小未知");
            }

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, childWidthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, childHeightMode);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        setMeasuredDimension(mParentWidth, mParentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mPullView = (ViewGroup) getChildAt(0);
        mPullWidth = mPullView.getMeasuredWidth();
        mPullHeight = mPullView.getMeasuredHeight();


        mRange = mPullHeight;
        mMiddleLine = (int) (mRange * 0.5f);

        mContent = getChildAt(1);
        mContentWidth = mContent.getMeasuredWidth();
        mContentHeight = mContent.getMeasuredHeight();


        if (state == STATE_CLOSE) {
            mPullView.layout(l, t - mPullHeight, l + mPullWidth, t);
            mContent.layout(l, t, l + mContentWidth, t + mContentHeight);
        } else if (state == STATE_OPEN) {
            mPullView.layout(l, t, l + mPullWidth, t + mPullHeight);
            mContent.layout(l, t + mRange, l + mContentWidth, t + mContentHeight + mRange);
        }
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private void onPullOverUIChange() {
        mTvRefresh.setText("松开刷新");
    }

    private void onPullBackUIChange() {
        mTvRefresh.setText("下拉刷新");
    }

    private void onRrefeshUIChange() {
        mTvRefresh.setText("正在刷新");
    }
}
