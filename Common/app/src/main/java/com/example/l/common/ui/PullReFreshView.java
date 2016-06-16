package com.example.l.common.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
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

    private View mContent;
    private ViewGroup mPullView;

    private int mParentHeight;
    private int mParentWidth;
    private int mContentWidth;
    private int mContentHeight;

    public static final int STATE_CLOSE = 1;
    public static final int STATE_OPEN = 2;
    public static final int STATE_REFRESH = 3;
    public static final int VISIBLE = 4;
    public static final int UNVISIBLE = 5;
    private int mPullState = STATE_CLOSE;
    private int mPullVisible = UNVISIBLE;
    private int mPullHeight;
    private int mLoadMoreState = STATE_CLOSE;
    private int mLoadMoreVisible = UNVISIBLE;
    private int mLoadMoreHeight;
    private int mLoadMoreWidth;

    private int mPullWidth;
    /**
     * 这个标志位很关键，因为view.offsetTopAndBottom api不会调用父类的onlayout方法，如果在view的offsetTopAndBottom方法执行过程
     * 中TextView刷新UI等，会调用父类的onLayout方法。
     * <p/>
     * 是初始化onlayout，还是刷新界面过程中进行的onlayout，true代表动画中因刷新UI导致的重新layout
     */
    private boolean isCalledByRevisedUI = false;

    @Bind(R.id.view_tv_pull_refresh)
    TextView mTvRefresh;

    private float mInitialMotionY;
    private Context mContext;
    private Scroller mScroller;
    private boolean mEnablePullRefresh = true;
    private OnRefreshListener mListener;
    private float mStartY = -1;
    private View mDragView;
    private boolean isAnimationStart = false;
    private boolean mEnableLoadMore;
    private ViewGroup mLoadMore;

    public PullReFreshView(Context context) {
        this(context, null);

    }

    public PullReFreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPullView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_pull_refresh, this, true);
        mLoadMore = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_load_more, this, true);
        ButterKnife.bind(this);
        mScroller = new Scroller(mContext);
    }

    public void onViewReleased(View releasedChild, float xvel, float yvel) {
        if (releasedChild == mContent) {
            int top = releasedChild.getTop();
            if (top >= mPullHeight && (mPullState == STATE_OPEN || mPullState == STATE_REFRESH)) {
                open();
            } else if (top < mPullHeight && mPullState == STATE_CLOSE) {
                complete();
            }
        }
    }


    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
        if (changedView == mContent) {
            if (dy < 0) {
                dy = dy * 2;
                if (top + dy < 0) {
                    dy = -top;
                }
            } else {
                dy = dy / 2;
            }

            if (top + dy > 0) {
                mPullVisible = VISIBLE;
            } else {
                mPullVisible = UNVISIBLE;
            }

            mPullView.offsetTopAndBottom(dy);
            mContent.offsetTopAndBottom(dy);
            if (top > mPullHeight && top + dy <= mPullHeight && mPullState == STATE_OPEN) {
                onPullBackUIChange();
                mPullState = STATE_CLOSE;
                isCalledByRevisedUI = true;
            } else if (top <= mPullHeight && top + dy >= mPullHeight && mPullState == STATE_CLOSE) {
                onPullOverUIChange();
                mPullState = STATE_OPEN;
                isCalledByRevisedUI = true;
            } else {
                invalidate();
            }
        }
    }


    private void open() {
        if (smoothSlideViewTo(mPullView, 0, 0) && smoothSlideViewTo(mContent, 0, mPullHeight)) {
            invalidate();
        }

        if (mPullState == STATE_OPEN) {
            mPullState = STATE_REFRESH;
            onRrefeshUIChange();
        } else if (mPullState == STATE_REFRESH) {
            //正在刷新 不进行重复刷新 编写代码请慎重
        }
    }

    public void complete() {
        if (smoothSlideViewTo(mPullView, 0, -mPullHeight) && smoothSlideViewTo(mContent, 0, 0)) {
            invalidate();
        }
        mPullState = STATE_CLOSE;
        onPullBackUIChange();
    }

    private boolean smoothSlideViewTo(View target, int finalLeft, int finalTop, int duration) {
        int startLeft = target.getLeft();
        int startTop = target.getTop();

        int dx = finalLeft - startLeft;
        int dy = finalTop - startTop;

        if (dx == 0 && dy == 0) {
            mScroller.abortAnimation();
            return false;
        }

        mScroller.startScroll(startLeft, startTop, dx, dy, duration);
        return true;
    }

    private boolean smoothSlideViewTo(View target, int finalLeft, int finalTop) {
        int startTop = target.getTop();
        int dy = finalTop - startTop;
        return smoothSlideViewTo(target, finalLeft, finalTop, Math.abs(dy) * 1);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isAnimationStart){
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = ev.getY();
                mDragView = null;
                onTouchEvent(ev);
                mContent.dispatchTouchEvent(adjustLocationForChild(ev));
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                float offset = moveY - mStartY;

                //1. 初始化页面下拉刷新，scrollView不动，scrollView可以向上滑动，点击正常  需要下发的事件，
                if (canChildScrollUp(mContent) && mPullVisible == UNVISIBLE) {
                    Log.d("Log_text", "PullReFreshView+dispatchTouchEvent+分给子child");
                    //可以向上或者向下滑动，点击正常，完全发送
                    adjustLocationForChild(ev);
                    onDragViewChange(mContent, ev);
                    mContent.dispatchTouchEvent(ev);
                    Log.d("Log_text", "PullReFreshView+dispatchTouchEvent 内容可以上下滑动");
                } else {
                    //不可以滑动，点击正常，又分为两种，第一种是不刷新状态，另外一种是刷新状态
                    if (mPullState == STATE_REFRESH) {
                        //比较复杂
                        if (mPullVisible == VISIBLE) {
                            //将事件分发给自己
                            Log.d("Log_text", "PullReFreshView+dispatchTouchEven 正在刷新状态 标题可见状态");
                            onDragViewChange(this, ev);
                            onTouchEvent(ev);
                        } else if (mPullVisible == UNVISIBLE) {
                            //为两种情况，向上还是向下
                            if (offset > 0) {
                                onDragViewChange(this, ev);
                                Log.d("Log_text", "PullReFreshView+dispatchTouchEven 正在刷新状态 标题正好消失，且向下滑动");
                                onTouchEvent(ev);
                            } else if (offset < 0) {
                                adjustLocationForChild(ev);
                                Log.d("Log_text", "PullReFreshView+dispatchTouchEven 正在刷新状态 标题正好消失，且向上滑动");
                                onDragViewChange(mContent, ev);
                                mContent.dispatchTouchEvent(ev);
                            }
                        } else {
                            Log.d("Log_text", "PullReFreshView+dispatchTouchEvent有问题分发");
                        }
                    } else if (mPullState == STATE_CLOSE) {
                        //也分为两种，第一种向上滑动，分给mContent，下拉刷新分给自己
                        if (offset > 0 ||(offset < 0 && mPullVisible == VISIBLE)) {
                            Log.d("Log_text", "PullReFreshView+dispatchTouchEvent标题消失状态，且向下滑");
                            onDragViewChange(this, ev);
                            onTouchEvent(ev);
                        } else if (offset < 0 && mPullVisible == UNVISIBLE) {
                            Log.d("Log_text", "PullReFreshView+dispatchTouchEvent标题消失状态，且向上滑");
                            adjustLocationForChild(ev);
                            onDragViewChange(mContent, ev);
                            mContent.dispatchTouchEvent(ev);
                        } else {
                            Log.d("Log_text", "PullReFreshView+dispatchTouchEvent有问题分发");
                        }

                    } else {
                        Log.d("Log_text", "PullReFreshView+dispatchTouchEvent 下拉状态，标题可见");
                        onDragViewChange(this, ev);
                        onTouchEvent(ev);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                onTouchEvent(ev);
                mContent.dispatchTouchEvent(adjustLocationForChild(ev));
                break;
        }
        return true;
    }

    private MotionEvent adjustLocationForChild(MotionEvent ev) {
        int mContentTop = mContent.getTop();
        float y = ev.getY();
        float adjustY = y - mContentTop;
        ev.setLocation(ev.getX(), adjustY);
        return ev;
    }

    public void onDragViewChange(View newDragView, MotionEvent event) {
        if (newDragView == mDragView) {
            return;
        }

        if (mDragView == null) {
            mDragView = newDragView;
            return;
        }
        mDragView = newDragView;
        event.setAction(MotionEvent.ACTION_DOWN);
        newDragView.dispatchTouchEvent(event);

        event.setAction(MotionEvent.ACTION_MOVE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d("Log_text", "PullReFreshView+onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                int offset = (int) (endY - mInitialMotionY);
                onViewPositionChanged(mContent, 0, mContent.getTop(), 0, offset);
                mInitialMotionY = endY;
                break;
            case MotionEvent.ACTION_UP:
                onViewReleased(mContent, 0, 0);
                mInitialMotionY = 0;
                break;
        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mParentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        mParentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int maxChildWidth = 0;
        int totalChildHeight = 0;

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

            int actualChildWidth = child.getMeasuredWidth();

            if (actualChildWidth > maxChildWidth) {
                maxChildWidth = actualChildWidth;
            }

            if (i == 2) {
                totalChildHeight = child.getMeasuredHeight();
            }
        }

        switch (parentHeightMode) {
            case MeasureSpec.AT_MOST:
                if (totalChildHeight < mParentHeight) {
                    mParentHeight = totalChildHeight;
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                mParentHeight = totalChildHeight;
                break;
        }

        switch (parentWidthMode) {
            case MeasureSpec.AT_MOST:
                if (maxChildWidth < mParentWidth) {
                    mParentWidth = maxChildWidth;
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                mParentWidth = maxChildWidth;
                break;
        }

        setMeasuredDimension(mParentWidth, mParentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mPullView = (ViewGroup) getChildAt(0);
        mPullWidth = mPullView.getMeasuredWidth();
        mPullHeight = mPullView.getMeasuredHeight();

        mContent = getChildAt(2);

        if(mContent == null){
            throw new IllegalArgumentException("PullReFreshView must have a child");
        }
        mContentWidth = mContent.getMeasuredWidth();
        mContentHeight = mContent.getMeasuredHeight();

        if (isCalledByRevisedUI) {
            mPullView.layout(l, t, l + mPullWidth, t + mPullHeight);
            mContent.layout(l, t + mPullHeight, l + mContentWidth, t + mContentHeight + mPullHeight);
            isCalledByRevisedUI = false;
        } else {
            if (mPullState == STATE_CLOSE) {
                mPullView.layout(l, t - mPullHeight, l + mPullWidth, t);
                mContent.layout(l, t, l + mContentWidth, t + mContentHeight);
            } else if (mPullState == STATE_OPEN) {
                mPullView.layout(l, t, l + mPullWidth, t + mPullHeight);
                mContent.layout(l, t + mPullHeight, l + mContentWidth, t + mContentHeight + mPullHeight);
            }
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            isAnimationStart = true;
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();
            int mContentTop = mContent.getTop();
            int mContentLeft = mContent.getLeft();

            mContent.offsetLeftAndRight(currX - mContentLeft);
            mContent.offsetTopAndBottom(currY - mContentTop);
            mPullView.offsetTopAndBottom(currY - mContentTop);
            mPullView.offsetLeftAndRight(currX - mContentLeft);

            invalidate();
        }else{
            isAnimationStart = false;
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
        if (mListener != null) {
            mListener.onRefresh();
        }
    }


    public boolean canChildScrollUp(View Target) {
        return ViewCompat.canScrollVertically(Target, -1);
    }

    public boolean canChildScrollDown(View Target) {
        return ViewCompat.canScrollVertically(Target, 1);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public void setPullRefreshEnable(boolean enablePullRefresh) {
        mEnablePullRefresh = enablePullRefresh;
    }

    public void setLoadMoreEnable(boolean enableLoadMore){
        mEnableLoadMore = enableLoadMore;
    }


}
