package com.example.l.common.widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author:dongpo 创建时间: 2016/4/17
 * 描述:
 * 修改:
 */
public class SlidingLayout extends ViewGroup {

    private int mParentHeight;
    private int mParentWidth;
    private ViewDragHelper mHelper;
    private ViewDragHelper mMHelper;
    private int mRange;
    private int middleLine;
    private ViewGroup mMenu;
    private ContentLinearLayout mContent;
    private int mContentWidth;
    private int mContentHeight;
    private int mMenuWidth;
    private int mMenuHeight;
    private int mTotaldx;
    private onStateChangeListener onStateChangeListener;

    private boolean ismContentLoseFocus=false;

    public void setOnStateChangeListener(SlidingLayout.onStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public static final int STATE_CLOSE=1;
    public static final int STATE_OPEN=2;
    public static final int STATE_OPENING=3;
    private int state=STATE_CLOSE;

    public interface onStateChangeListener{
        void onOpen();
        void onColse();
        void onOpening(float percent);
    }

    public SlidingLayout(Context context) {
        this(context,null);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMHelper = ViewDragHelper.create(this,new DragCallBack());
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMHelper = ViewDragHelper.create(this,new DragCallBack());
    }



    public class DragCallBack extends ViewDragHelper.Callback{
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child==mContent){
                return reCorrectPosition(left);
            }else{
                return reCorrectMenuPosition(left);
            }

        }

        private int reCorrectMenuPosition(int left) {
            //[-width,-width+mRange]
            if(left<-mMenuWidth){
                left=-mMenuWidth;
            }
            if(left>mRange-mMenuWidth){
                left=mRange-mMenuWidth;
            }
            return left;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if(dx==0){
                return;
            }
            state=STATE_OPENING;
            if(changedView==mContent){
                mMenu.offsetLeftAndRight(dx);
                invalidate();
            }

            if(changedView==mMenu){
                mContent.offsetLeftAndRight(dx);
                invalidate();
            }

            mTotaldx+=dx;
            float percent=mTotaldx*1.0f/mRange;
            if(onStateChangeListener!=null){
                onStateChangeListener.onOpening(percent);
            }

            //[0,1]
            mContent.setScaleX(1f-0.2f*percent);//[1,0.8]
            mContent.setScaleY(1f-0.2f*percent);//[1,0.8]
            //mContent.setAlpha(1f-0.2f*percent);


            //[0.6,1]
            mMenu.setScaleX(0.8f+0.2f*percent);//[0.6,1]
            mMenu.setScaleY(0.8f+0.2f*percent);//[0.6,1]
            mMenu.setAlpha(0.8f+0.2f*percent);

            float tranX=(mMenuWidth*0.2f)*percent;

            mMenu.setTranslationX(tranX/2);


        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int left = mContent.getLeft();

                if(left<middleLine&&xvel<=0){
                    close(true);
                }else if(xvel<0){
                    close(true);
                }else{
                    open();
                }

        }
    }


        private int reCorrectPosition(int left) {
            if(left<0){
                left=0;
            }
            if(left>mRange){
                left=mRange;
            }
            return left;
        }




    public void close(boolean smooth) {
        if(smooth){
            if(mMHelper.smoothSlideViewTo(mContent,0,0)&&mMHelper.smoothSlideViewTo(mMenu,-mMenuWidth,0)){
                invalidate();
            }
        }else{
            mContent.layout(0,0,mContentWidth,mContentHeight);
            mContent.requestLayout();
        }
        state=STATE_CLOSE;
    }

    public void open() {
        if(mMHelper.smoothSlideViewTo(mContent,mRange,0)&&mMHelper.smoothSlideViewTo(mMenu,mRange-mMenuWidth,0)){
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if(mMHelper.continueSettling(true)){
            invalidate();
        }else{
            int left = mContent.getLeft();
            if(left==0&&state==STATE_OPENING){
                state=STATE_CLOSE;
                if(onStateChangeListener!=null){
                    onStateChangeListener.onColse();
                    mContent.setIntercepted(false);
                }
            }else if(left==mRange&&state==STATE_OPENING){
                state=STATE_OPEN;
                if(onStateChangeListener!=null){
                    onStateChangeListener.onOpen();
                    mContent.setIntercepted(true);
                }
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mMenu = (ViewGroup) getChildAt(0);
        mMenuWidth = mMenu.getMeasuredWidth();
        mMenuHeight = mMenu.getMeasuredHeight();



        mRange = mMenuWidth;
        middleLine= (int) (mRange*0.5f);

        mContent = (ContentLinearLayout) getChildAt(1);
        mContentWidth = mContent.getMeasuredWidth();
        mContentHeight = mContent.getMeasuredHeight();


        if(state==STATE_CLOSE){
            mMenu.layout(l-mMenuWidth,t,l,t+ mMenuHeight);
            mContent.layout(l,t,l+ mContentWidth,t+ mContentHeight);
        }else if(state==STATE_OPEN){
            mMenu.layout(l-mMenuWidth+mRange,t,l+mRange,t+ mMenuHeight);
            mContent.layout(l+mRange,t,l+ mContentWidth+mRange,t+ mContentHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mParentWidth = MeasureSpec.getSize(widthMeasureSpec);
        mParentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i <childCount; i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = child.getLayoutParams();

            int childWidthMode = MeasureSpec.UNSPECIFIED;
            int childHeightMode = MeasureSpec.UNSPECIFIED;

            int childWidth = 0;
            int childHeight = 0;
            if(layoutParams.height>0){
                childHeightMode = MeasureSpec.EXACTLY;
                childHeight =layoutParams.height;
            }else if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
                childHeightMode = MeasureSpec.AT_MOST;
                childHeight =mParentHeight;
            } else if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                childHeightMode = MeasureSpec.EXACTLY;
                childHeight =mParentHeight;
            }else{
                throw new RuntimeException("子view的大小未知");
            }


            if(layoutParams.width>0){
                childWidthMode = MeasureSpec.EXACTLY;
                childWidth =layoutParams.width;
            }else if (layoutParams.width == LayoutParams.WRAP_CONTENT) {
                childWidthMode = MeasureSpec.AT_MOST;
                childWidth =mParentWidth;
            } else if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                childWidthMode = MeasureSpec.EXACTLY;
                childWidth =mParentWidth;
            }else{
                throw new RuntimeException("子view的大小未知");
            }

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth,childWidthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight,childHeightMode);
            child.measure(childWidthMeasureSpec,childHeightMeasureSpec);
        }

        setMeasuredDimension(mParentWidth,mParentHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       return mMHelper.shouldInterceptTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mMHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
