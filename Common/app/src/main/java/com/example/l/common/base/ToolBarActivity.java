package com.example.l.common.base;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.l.common.R;
import com.example.l.common.ui.dialog.DialogControl;
import com.example.l.common.utils.TDevice;

import org.kymjs.kjframe.utils.StringUtils;


/**
 * baseActionBar Activity
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 上午11:30:15 引用自：tonlin
 */
public class ToolBarActivity extends BaseActivity implements
        DialogControl {

    public Toolbar mToolbar;
    private ActionBar mActionBar;
    private TextView mTvActionTitle;
    private TextView mTvToolBarCenter;

    @Override
    public void initView() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        initActionBar(mActionBar);
    }


    @Override
    protected final View getLayoutView() {
        FrameLayout llContent = new FrameLayout(this);

        int layoutId = getLayoutId();
        mInflater.from(this).inflate(layoutId, llContent, true);
        mInflater.from(this).inflate(R.layout.tool_bar, llContent, true);

        mToolbar = (Toolbar) llContent.getChildAt(1);
        View customView = llContent.getChildAt(0);
        mTvToolBarCenter = (TextView) mToolbar.findViewById(R.id.common_toolbar_center);

        if(setStatusBarMode()){
            FrameLayout.LayoutParams toolBarParams = (FrameLayout.LayoutParams) mToolbar.getLayoutParams();
            toolBarParams.topMargin = TDevice.getStatusBarHeight();
            mToolbar.setLayoutParams(toolBarParams);
            mToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }else{
            FrameLayout.LayoutParams customViewParams = (FrameLayout.LayoutParams) customView.getLayoutParams();
            customViewParams.topMargin = TDevice.getActionBarHeight(this);
            customView.setLayoutParams(customViewParams);
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        llContent.setLayoutParams(params);
        return llContent;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && setStatusBarMode()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return 设置statusBar的模式，true为全屏模式（浸入式模式），false为统一模式
     * 默认为false。
     */
    protected boolean setStatusBarMode(){
        return false;
    }

    protected boolean hasBackButton() {
        return true;
    }

    protected void initActionBar(ActionBar actionBar) {
        if (actionBar == null)
            return;
        if (hasBackButton()) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        } else {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setDisplayUseLogoEnabled(false);
        }
    }

    public void setActionBarTitle(int resId) {
        if (resId != 0) {
            setActionBarTitle(getString(resId));
        }
    }

    public void setActionBarTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            mActionBar.setDisplayShowTitleEnabled(false);
        }
        if (mActionBar != null) {
            if (mTvActionTitle != null) {
                mTvActionTitle.setText(title);
            }
            mActionBar.setTitle(title);
        }
    }

    public void setActionBarBackButtonIcon(int resId) {
        mToolbar.setNavigationIcon(resId);
    }

    public void setActionBarTitleColor(int color) {
        mToolbar.setTitleTextColor(getResources().getColor(color));
    }

    public void setActionBarCenterTitle(int resId){
        mTvToolBarCenter.setText(resId);
    }

    public void setActionBarCenterTextColor(int resId){
        mTvToolBarCenter.setTextColor(getResources().getColor(resId));
    }

    public void setActionBarCenterTextSize(float size){
        mTvToolBarCenter.setTextSize(size);
    }

    public void addActionBarCustomView(int layoutId){
        mInflater.inflate(layoutId,mToolbar,true);
    }
}
