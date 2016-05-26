package com.example.l.common.manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.l.common.application.AppContext;
import com.example.l.common.bean.Update;
import com.example.l.common.utils.DialogHelp;
import com.example.l.common.utils.WindowUtils;

/**
 * 更新管理类
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年11月18日 下午4:21:00
 */

public class UpdateManager {

    private Update mUpdate;

    private Context mContext;

    private boolean isShow = false;

    private ProgressDialog _waitDialog;

    /*private AsyncHttpResponseHandler mCheckUpdateHandle = new AsyncHttpResponseHandler() {

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            hideCheckDialog();
            if (isShow) {
                showFaileDialog();
            }
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            hideCheckDialog();
            mUpdate = XmlUtils.toBean(Update.class,
                    new ByteArrayInputStream(arg2));

            onFinshCheck();
        }
    };*/

    public UpdateManager(Context context, boolean isShow) {
        this.mContext = context;
        this.isShow = isShow;
    }

    public boolean haveNew() {
        if (this.mUpdate == null) {
            return false;
        }
        boolean haveNew = false;
        int curVersionCode = WindowUtils.getVersionCode(AppContext
                .getInstance().getPackageName());
        if (curVersionCode < mUpdate.versionCode) {
            haveNew = true;
        }
        return haveNew;
    }

    public void checkUpdate() {
        //请求网络
    }

    private void onFinshCheck() {
        if (haveNew()) {
            showUpdateInfo();
        } else {
            if (isShow) {
                showLatestDialog();
            }
        }
    }

    private void showCheckDialog() {
        if (_waitDialog == null) {
            _waitDialog = DialogHelp.getWaitDialog((Activity) mContext, "正在获取新版本信息...");
        }
        _waitDialog.show();
    }

    private void hideCheckDialog() {
        if (_waitDialog != null) {
            _waitDialog.dismiss();
        }
    }

    private void showUpdateInfo() {
        if (mUpdate == null) {
            return;
        }
        AlertDialog.Builder dialog = DialogHelp.getConfirmDialog(mContext, "新版本快来下载吧", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击升级
            }
        });
        dialog.setTitle("发现新版本");
        dialog.show();
    }

    private void showLatestDialog() {
        DialogHelp.getMessageDialog(mContext, "已经是新版本了").show();
    }

    private void showFaileDialog() {
        DialogHelp.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
    }
}
