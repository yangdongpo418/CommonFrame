package com.example.l.common.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.l.common.R;

import java.util.List;

/**
 * @author:dongpo 创建时间: 6/30/2016
 * 描述:
 * 修改:
 */
public class NumberTimePicker extends RecyclerView {

    public int recyclePosition = 1;
    public boolean wrapMode = false;
    private TimePickerAdapter mAdapter;

    private static final int EMPTY_VIEW = 1;
    private static final int PICKER_VIEW = 2;
    private int mParentHeight;
    private int mChildHeight;
    private Scroller mScroller;
    private int mLastY;
    private OnChooseListener mChooseListener;
    private int state = RecyclerView.SCROLL_STATE_IDLE;
    private boolean isAnimationStart = false;

    public NumberTimePicker(Context context) {
        super(context);
    }

    public NumberTimePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(getContext());

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        adjustPosition();
                        state = SCROLL_STATE_IDLE;
                        break;
                    case SCROLL_STATE_DRAGGING:
                        state = SCROLL_STATE_DRAGGING;
                        break;
                    case SCROLL_STATE_SETTLING:
                        state = SCROLL_STATE_SETTLING;
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (state == SCROLL_STATE_SETTLING || state == SCROLL_STATE_DRAGGING) {
                    if (!canScrollVertically(-1)) {
                        recyclePosition = 1;
                        if (mChooseListener != null) {
                            mChooseListener.onItemChoose(getOutPosition(recyclePosition));
                        }
                    } else if (!canScrollVertically(1)) {
                        recyclePosition = mAdapter.getItemCount() - 2;
                        if (mChooseListener != null) {
                            mChooseListener.onItemChoose(getOutPosition(recyclePosition));
                        }
                    }
                }


                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    int top = child.getTop();
                    int start = (mParentHeight - mChildHeight) / 2 - mChildHeight;
                    int middle = (mParentHeight - mChildHeight) / 2;
                    int end = middle + mChildHeight;

                    if (top > start && top < end) {
                        int distance = Math.abs(top - middle);
                        float percent = distance * 1.0f / mChildHeight;
                        startAnimations(child, percent);
                    } else {
                        restoreAnimaton(child);
                    }
                }

            }

        });
    }

    private void restoreAnimaton(View child) {
        float scaleY = child.getScaleY();
        float scaleX = child.getScaleX();
        if (scaleX != 1) {
            child.setScaleX(1);
        }

        if (scaleY != 1) {
            child.setScaleY(1);
        }
    }


    private void startAnimations(View child, float percent) {
        //从上往下,是从[0,1]  越大越远 越远越小
        percent = (float) ((1 - percent) * 0.6 + 1);
        child.setScaleX(percent);
        child.setScaleY(percent);

    }

    private void adjustPosition() {
        mLastY = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int top = child.getTop();
            int position = getChildAdapterPosition(child);
            int start = (mParentHeight - mChildHeight) / 2;
            int end = start + mChildHeight;
            int middle = start + mChildHeight / 2;

            if (top > start && top < middle) {
                recyclePosition = position;
                mScroller.startScroll(0, top, 0, top - start, 400);
                invalidate();
                if (mChooseListener != null) {
                    mChooseListener.onItemChoose(getOutPosition(recyclePosition));
                }
                break;
            } else if (top >= middle && top < end) {
                recyclePosition = position - 1;
                mScroller.startScroll(0, top, 0, top - end, 400);
                invalidate();
                if (mChooseListener != null) {
                    mChooseListener.onItemChoose(getOutPosition(recyclePosition));
                }
                break;
            }
        }
    }

    public class TimePickerAdapter extends RecyclerView.Adapter {


        private List<String> mData;


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = null;
            if (viewType == EMPTY_VIEW) {
                View view = new View(parent.getContext());
                RecyclerView.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (mParentHeight - mChildHeight) / 2);
                view.setLayoutParams(params);
                holder = new EmptyHolder(view);

            } else if (viewType == PICKER_VIEW) {
                TextView pickView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recycle_picker, parent, false);
                holder = new PickerHolder(pickView);
                pickView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getChildAdapterPosition(v);
                        scrollIntItem(position - recyclePosition);
                    }
                });
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder instanceof EmptyHolder) {

            } else if (holder instanceof PickerHolder) {
                if (wrapMode == false) {
                    position = position - 1;
                } else {
                    position = position % mData.size();
                }
                PickerHolder pickerHolder = (PickerHolder) holder;
                String data = mData.get(position);
                pickerHolder.mPickView.setText(data);
            }
        }

        @Override
        public int getItemCount() {
            if (wrapMode == false) {
                return mData.size() + 2;
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public int getItemViewType(int position) {
            if (wrapMode == false) {
                if (position == 0 || position == getItemCount() - 1) {
                    return EMPTY_VIEW;
                }
                return PICKER_VIEW;
            }
            return PICKER_VIEW;
        }

        public void setData(List<String> data) {
            mData = data;
        }
    }

    public static class PickerHolder extends RecyclerView.ViewHolder {

        private final TextView mPickView;

        public PickerHolder(View itemView) {
            super(itemView);
            mPickView = (TextView) itemView;
        }


    }

    public static class EmptyHolder extends RecyclerView.ViewHolder {

        private final View mEmptyView;

        public EmptyHolder(View itemView) {
            super(itemView);
            mEmptyView = itemView;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAdapter = new TimePickerAdapter();
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(layout);
        setAdapter(mAdapter);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void addOnChooseListener(OnChooseListener chooseListener) {
        mChooseListener = chooseListener;
    }

    public interface OnChooseListener {
        void onItemChoose(int position);
    }

    public void setData(List<String> datas) {
        mAdapter.setData(datas);
        PickerHolder holder = (PickerHolder) mAdapter.onCreateViewHolder(this, PICKER_VIEW);
        mChildHeight = holder.mPickView.getLayoutParams().height;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        mParentHeight = getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (isAnimationStart == false) {
                isAnimationStart = true;
            }

            int currY = mScroller.getCurrY();
            int startY = mScroller.getStartY();
            int offset = 0;
            if (mLastY != 0) {
                offset = currY - mLastY;
            } else {
                offset = currY - startY;
            }

            scrollBy(0, offset);
            invalidate();
            mLastY = currY;
        } else {
            isAnimationStart = false;
        }
    }

    private int getOutPosition(int recyclePosition) {
        return recyclePosition - 1;
    }

    private void scrollIntItem(int number) {
        if (isAnimationStart == true) {
            return;
        }
        recyclePosition += number;
        mLastY = 0;
        int top = mChildHeight * (recyclePosition - 1) + (mParentHeight - mChildHeight) / 2;
        mScroller.startScroll(0, top, 0, number * mChildHeight, 200);
        invalidate();
        if (mChooseListener != null) {
            mChooseListener.onItemChoose(getOutPosition(recyclePosition));
        }
    }


}
