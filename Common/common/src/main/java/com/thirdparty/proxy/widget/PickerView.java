package com.thirdparty.proxy.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Scroller;
import android.widget.TextView;

import com.thirdparty.proxy.R;

import java.util.List;

/**
 * @author:dongpo 创建时间: 6/30/2016
 * 描述:
 * 修改:
 */
public class PickerView extends RecyclerView {

    /**------------recycleView当前的索引----------------*/
    public int recyclePosition = 1;
    /**------------是否是无限循环模式----------------*/
    public boolean mWrapMode = false;
    private TimePickerAdapter mAdapter;

    private static final int EMPTY_VIEW = 1;
    private static final int PICKER_VIEW = 2;
    /**------------recycleView的高度----------------*/
    private int mParentHeight;
    /**------------每个条目的高度----------------*/
    private int mChildHeight;
    private Scroller mScroller;
    /**------------平滑移动用的scrollBy，记录上一次Y的值，每次使用scroller进行平滑移动，需重置此值----------------*/
    private int mLastY;
    private OnChooseListener mChooseListener;
    /**------------是否有动画在执行----------------*/
    private boolean isAnimationStart = false;
    /**------------数据源----------------*/
    private List<String> mDatas;
    private int mStart;
    private int mMiddle;
    private int mEnd;

    public PickerView(Context context) {
        super(context);
    }

    public PickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(getContext());

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        //滑动结束的时候，进行位置校验
                        adjustPosition();
                        break;
                    case SCROLL_STATE_DRAGGING:
                        break;
                    case SCROLL_STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int state = getScrollState();
                if (mWrapMode == false && (state == SCROLL_STATE_SETTLING || state == SCROLL_STATE_DRAGGING)) {
                    //当滑动到顶部或者底部不行再滑动的时候，进行单独回调
                    if (!canScrollVertically(-1)) {
                        recyclePosition = 1;
                        if (mChooseListener != null) {
                            mChooseListener.onItemChoose(mDatas, getOutPosition(recyclePosition));
                        }
                    } else if (!canScrollVertically(1)) {
                        recyclePosition = mAdapter.getItemCount() - 2;
                        if (mChooseListener != null) {
                            mChooseListener.onItemChoose(mDatas, getOutPosition(recyclePosition));
                        }
                    }
                }


                //遍历所有的View，进行动画
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    int top = child.getTop();
                    mStart = (mParentHeight - mChildHeight) / 2 - mChildHeight;
                    mMiddle = (mParentHeight - mChildHeight) / 2;
                    mEnd = mMiddle + mChildHeight;

                    int distance = Math.abs(top - mMiddle);
                    float percent = distance * 1.0f / mChildHeight;
                    if (top > mStart && top < mEnd) {
                        //当View的高度满足这个区间，进行字体放大
                        startScaleAnimations(child, percent);
                    } else {
                        //滑动过快的时候，有可能导致恢复大小不全，进行手动恢复
                        restoreScaleAnimaton(child);
                    }

                    //无论什么情况，都进行字体透明度动画
                    float alaphPercent = distance * 1.0f / (2 * mChildHeight);
                    startAlaphAnimation(child, alaphPercent);


                }

            }

        });

       //当布局完毕之后，进行的一些初始化
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initRecyclePosition();
                if (mChooseListener != null) {
                    //进行初始化回调，即刚进入页面的时候，会回调
                    mChooseListener.onItemChoose(mDatas, getOutPosition(recyclePosition));
                }

                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

    }

    public void initRecyclePosition(){
        if (mWrapMode == true) {
            //无限循环模式，就将初始化的索引设置成一个中间值，并scroll到那里去
            recyclePosition = (Integer.MAX_VALUE / mDatas.size()) / 2 * mDatas.size() + 2;
            scrollToPosition(recyclePosition);

        }else{
            recyclePosition = 1;
        }

    }

    /**
     * 开启一个字体缩放的动画
     * @param child
     * @param percent
     */
    private void startAlaphAnimation(View child, float percent) {
        percent = (float) ((1 - percent) * 0.7 + 0.3); //[1,0.8]
        child.setAlpha(percent);
    }

    /**
     * 恢复字体大小
     * @param child
     */
    private void restoreScaleAnimaton(View child) {
        float scaleY = child.getScaleY();
        float scaleX = child.getScaleX();
        if (scaleX != 1) {
            child.setScaleX(1);
        }

        if (scaleY != 1) {
            child.setScaleY(1);
        }
    }


    /**
     * 开启透明度动画
     * @param child
     * @param percent
     */
    private void startScaleAnimations(View child, float percent) {
        //从上往下,是从[0,1]  越大越远 越远越小
        percent = (float) ((1 - percent) * 0.6 + 1); //[1.6,1]
        child.setScaleX(percent);
        child.setScaleY(percent);

    }

    /**
     * 滑动结束的的时候，进行位置校验
     */
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
                //如果有一个控件的左上角，满足在中间view的偏上方的位置，就让他占据中间位置，并回调
                recyclePosition = position;
                mScroller.startScroll(0, top, 0, top - start, 400);
                invalidate();
                if (mChooseListener != null) {
                    mChooseListener.onItemChoose(mDatas, getOutPosition(recyclePosition));
                }
                break;
            } else if (top >= middle && top < end) {
                //如果在偏下位置，就让上一个view占据中间位置
                recyclePosition = position - 1;
                mScroller.startScroll(0, top, 0, top - end, 400);
                invalidate();
                if (mChooseListener != null) {
                    mChooseListener.onItemChoose(mDatas, getOutPosition(recyclePosition));
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
                pickView.setOnClickListener(new View.OnClickListener() {
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
                if (mWrapMode == false) {
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
            if (mWrapMode == false) {
                if (mData == null) {
                   return 0;
                }
                return mData.size() + 2;
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public int getItemViewType(int position) {
            if (mWrapMode == false) {
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

    /**
     * 完成inflate回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAdapter = new TimePickerAdapter();
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(layout);
        setAdapter(mAdapter);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        //禁止overScrollBy的阴影效果
    }

    public void addOnChooseListener(OnChooseListener chooseListener) {
        mChooseListener = chooseListener;
    }

    public interface OnChooseListener {
        void onItemChoose(List<String> data, int position);
    }

    /**
     * 给picker设置数据
     * @param datas
     */
    public void setData(List<String> datas) {
        mDatas = datas;
        mAdapter.setData(datas);
        setAdapter(mAdapter);
        initRecyclePosition();
        //之所以用setAdapter，是在数据更新的时候，直接刷新整个界面，个别刷新为导致动画罗乱掉
        PickerHolder holder = (PickerHolder) mAdapter.onCreateViewHolder(this, PICKER_VIEW);
        mChildHeight = holder.mPickView.getLayoutParams().height;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        mParentHeight = getMeasuredHeight();
    }

    /**
     * 平滑移动
     */
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

    /**
     * 将position暴露出去，必须调用的方法
     * @param recyclePosition
     * @return
     */
    private int getOutPosition(int recyclePosition) {
        if (mWrapMode == true) {
            //如果为无限循环模式，就进行取余
            return recyclePosition % mDatas.size();
        }
        // -1是因为有限模式，头部是用了一个空的view来填充的
        return recyclePosition - 1;
    }

    /**
     * 向上滑动或者向下滑动几个单位
     * @param number +代表向下， -代表向上。
     */
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
            mChooseListener.onItemChoose(mDatas, getOutPosition(recyclePosition));
        }
    }

    /**
     * 设置是否为无限模式
     * @param wrapMode
     */
    public void setWrapMode(boolean wrapMode) {
        this.mWrapMode = wrapMode;
        mAdapter.notifyDataSetChanged();
    }
}
