package com.example.l.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.example.l.common.R;
import com.example.l.common.manager.ActivityManager;
import com.example.l.common.ui.dialog.CommonToast;
import com.example.l.common.ui.dialog.DialogControl;
import com.example.l.common.utils.DialogHelp;
import com.example.l.common.utils.WindowUtils;

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

        onBeforeSetContentLayout();

        View layoutView = getLayoutView();

        if (layoutView != null) {
            setContentView(layoutView);
        }
        // 通过注解绑定控件
        ButterKnife.bind(this);

        init(savedInstanceState);
        initView();
        initData();
        _isVisible = true;
    }

    protected void onBeforeSetContentLayout() {}

    protected int getLayoutId() {
        return 0;
    }

    protected View getLayoutView(){
        if(getLayoutId()!=0){
            return mInflater.inflate(getLayoutId(),null);
        }
        
        return null;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }


    protected void init(Bundle savedInstanceState) {}

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

    public void initView(){}
    public void initData(){}
}
