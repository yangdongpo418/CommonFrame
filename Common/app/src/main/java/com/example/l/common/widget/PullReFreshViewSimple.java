package com.example.l.common.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
public class PullReFreshViewSimple extends ViewGroup {

    private int mTouchSlop;
    private View mContent;
    private ViewGroup mPullView;
    private static final int INVALID_POINTER = -1;

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
    /**
     * 这个标志位很关键，因为view.offsetTopAndBottom api不会调用父类的onlayout方法，如果在view的offsetTopAndBottom方法执行过程
     * 中TextView刷新UI等，会调用父类的onLayout方法。
     * <p/>
     * 是初始化onlayout，还是刷新界面过程中进行的onlayout，true代表动画中因刷新UI导致的重新layout
     */
    private boolean isCalledByRevisedUI = false;

    @Bind(R.id.view_tv_pull_refresh)
    TextView mTvRefresh;

    private boolean mIsBeingDragged = false;
    private float mInitialMotionY;
    private Context mContext;
    private Scroller mScroller;
    private boolean mEnablePullRefresh = true;
    private OnRefreshListener mListener;
    private int mStartScrollY;

    public PullReFreshViewSimple(Context context) {
        this(context, null);

    }

    public PullReFreshViewSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPullView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_pull_refresh, this, true);
        ButterKnife.bind(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(mContext);
    }

    public void setContentView(View contentView) {
        addView(contentView, 1);
        mContent = contentView;
    }


    public void onViewReleased(View releasedChild, float xvel, float yvel) {
        if (releasedChild == mContent) {
            int top = releasedChild.getTop();
            if (top >= mPullHeight && (state == STATE_OPEN || state == STATE_REFRESH)) {
                open();
            } else if (top < mPullHeight && state == STATE_CLOSE) {
                complete();
            }
        }
    }


    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
        if (changedView == mContent) {
            mPullView.offsetTopAndBottom(dy);
            mContent.offsetTopAndBottom(dy);
            if (top > mPullHeight && top + dy <= mPullHeight && state == STATE_OPEN) {
                onPullBackUIChange();
                state = STATE_CLOSE;
                isCalledByRevisedUI = true;
            } else if (top <= mPullHeight && top + dy >= mPullHeight && state == STATE_CLOSE) {
                onPullOverUIChange();
                state = STATE_OPEN;
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

        if (state == STATE_OPEN) {
            state = STATE_REFRESH;
            onRrefeshUIChange();
        } else if (state == STATE_REFRESH) {
            //正在刷新 不进行重复刷新 编写代码请慎重
        }
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
        return smoothSlideViewTo(target, finalLeft, finalTop, Math.abs(dy) * 2);
    }


    public void complete() {
        if (smoothSlideViewTo(mPullView, 0, -mPullHeight) && smoothSlideViewTo(mContent, 0, 0)) {
            invalidate();
        }
        state = STATE_CLOSE;
        onPullBackUIChange();
    }

    public void closeAnimationImm() {
        if (smoothSlideViewTo(mPullView, 0, -mPullHeight, 0) && smoothSlideViewTo(mContent, 0, 0, 0)) {
            invalidate();
        }
    }

    /**
     * @param ev 当用户向下拖动，且里面的可滑动的控件不能再向下滑的时候，进行拦截
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("Log_text", "PullReFreshView+onInterceptTouchEvent");
        if (canChildScrollUp(mContent)) {
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                final float initialMotionY = ev.getY();
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;

            case MotionEvent.ACTION_MOVE:
                final float y = ev.getY();
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;

                if (yDiff > mTouchSlop && !mIsBeingDragged && mEnablePullRefresh) {
                    mIsBeingDragged = true;
                }
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Log_text", "PullReFreshView+onTouchEvent");
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

            if (i == 1) {
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


        mRange = mPullHeight;
        mMiddleLine = (int) (mRange * 0.5f);

        mContent = getChildAt(1);
        mContentWidth = mContent.getMeasuredWidth();
        mContentHeight = mContent.getMeasuredHeight();

        if (isCalledByRevisedUI) {
            mPullView.layout(l, t, l + mPullWidth, t + mPullHeight);
            mContent.layout(l, t + mRange, l + mContentWidth, t + mContentHeight + mRange);
            isCalledByRevisedUI = false;
        } else {
            if (state == STATE_CLOSE) {
                mPullView.layout(l, t - mPullHeight, l + mPullWidth, t);
                mContent.layout(l, t, l + mContentWidth, t + mContentHeight);
            } else if (state == STATE_OPEN) {
                mPullView.layout(l, t, l + mPullWidth, t + mPullHeight);
                mContent.layout(l, t + mRange, l + mContentWidth, t + mContentHeight + mRange);
            }
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();
            int mContentTop = mContent.getTop();
            int mContentLeft = mContent.getLeft();

            mContent.offsetLeftAndRight(currX - mContentLeft);
            mContent.offsetTopAndBottom(currY - mContentTop);
            mPullView.offsetTopAndBottom(currY - mContentTop);
            mPullView.offsetLeftAndRight(currX - mContentLeft);

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
        if (mListener != null) {
            mListener.onRefresh();
        }
    }


    public boolean canChildScrollUp(View Target) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (Target instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) Target;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return Target.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(Target, -1);
        }
    }

    public boolean childScrollUpWithin(View target, int distance) {
        if (!canChildScrollUp(target) || distance <= 0) {
            return false;
        }

        if (target instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) target;
            //第一个child的高度比parentView的padding值小
            int moveDistance = absListView.getChildAt(0)
                    .getTop() - absListView.getPaddingTop();
            return Math.abs(moveDistance) < distance;
        } else {
            return target.getScrollY() < distance;
        }
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
}
