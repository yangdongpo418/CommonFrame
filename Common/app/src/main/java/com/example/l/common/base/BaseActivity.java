package com.example.l.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.l.common.R;
import com.example.l.common.manager.ActivityManager;
import com.example.l.common.ui.dialog.CommonToast;
import com.example.l.common.ui.dialog.DialogControl;
import com.example.l.common.utils.DialogHelp;
import com.example.l.common.utils.WindowUtils;
import com.example.l.common.widget.LoadingStateView;

import butterknife.ButterKnife;

/**
 * @author:dongpo 创建时间: 5/24/2016
 * 描述:
 * 修改:
 */
public class BaseActivity extends AppCompatActivity implements DialogControl {
    private boolean _isVisible;
    private ProgressDialog _waitDialog;

    protected LayoutInflater mInflater;
    private LoadingStateView mLoadingStateView;
    private boolean mIsAddLoadingState = false;
    private View mTargetLoadingStateView;
    private boolean mIsPullRefreshEnable;
    private View mTargetPullRefreshView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        WindowUtils.hideSoftKeyboard(getCurrentFocus());
        ActivityManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getAppManager().addActivity(this);
        mInflater = getLayoutInflater();

        View layoutView = onBeforeSetContentLayout(getLayoutView());

        if (layoutView != null) {
            if(mIsAddLoadingState){
                layoutView = addLoadingStateView(layoutView, mTargetLoadingStateView);
            }
            setContentView(layoutView);
        }else{
            throw new IllegalArgumentException("contentView can't be null");
        }
        // 通过注解绑定控件
        ButterKnife.bind(this);

        restoreInit(savedInstanceState);
        initView();
        initData();
        _isVisible = true;
    }

    /**
     *
     * @param targetView  用户要添加加载状态的view
     * @param contentView  整个布局的View
     * @return
     * 添加加载状态
     */
    private View addLoadingStateView(View contentView,View targetView) {
        mLoadingStateView = new LoadingStateView(this);

        if(targetView == null){
            mLoadingStateView.addSuccessView(contentView);
            ViewGroup.LayoutParams contentParams = contentView.getLayoutParams();
            mLoadingStateView.setLayoutParams(contentParams);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.setLayoutParams(params);
            return mLoadingStateView;
        }else{
            ViewGroup parent = (ViewGroup)targetView.getParent();
            if(parent != null){
                int index = parent.indexOfChild(targetView);
                parent.removeView(targetView);
                mLoadingStateView.addSuccessView(targetView);
                ViewGroup.LayoutParams targetParams = targetView.getLayoutParams();
                parent.addView(mLoadingStateView,index,targetParams);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                targetView.setLayoutParams(params);
                return contentView;
            }else{
                throw new IllegalArgumentException("targetView must have a parent view");
            }
        }
    }

    public void updateViewState(int state){
        if(mLoadingStateView != null){
            mLoadingStateView.updateState(state);
        }
    }

    protected View onBeforeSetContentLayout(View contentView) {
        return contentView;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View getLayoutView() {
        if (getLayoutId() != 0) {
            return mInflater.inflate(getLayoutId(), null);
        }
        return null;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }


    /**
     * @param savedInstanceState activity被后台杀死，后恢复进行调用
     */
    protected void restoreInit(Bundle savedInstanceState) {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showToast(int msgResid, int icon, int gravity) {
        showToast(getString(msgResid), icon, gravity);
    }

    public void showToast(String message, int icon, int gravity) {
        CommonToast toast = new CommonToast(this);
        toast.setMessage(message);
        toast.setMessageIc(icon);
        toast.setLayoutGravity(gravity);
        toast.show();
    }

    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    @Override
    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public ProgressDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(this, message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @return 返回是否需要 添加加载的4种状态，如果targetView有parentView 就移除 用FrameLayout包一层，然后再添加
     * 如果没有parentView 则直接进行包一层
     */
    public final void setLoadingStateEnable(boolean isAddLoadingState, View targetView){
        mIsAddLoadingState = isAddLoadingState;
        mTargetLoadingStateView = targetView;
    }

    /**
     * @param isPullRefreshEnable
     * @param targetView  下拉刷新比较特殊，如果targetView为ListView，RecycleView，ScrollView等带滚动条的页面
     *                    必须将下拉刷新直接包在外层，否侧滑动失效
     */
    public final void setPullRefreshEnable(boolean isPullRefreshEnable, View targetView){
        mIsPullRefreshEnable = isPullRefreshEnable;
        mTargetPullRefreshView = targetView;
    }

    public void initView() {
    }

    public void initData() {
    }
}
