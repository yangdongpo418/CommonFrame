package com.example.l.common.ui;

import android.content.Context;
import android.content.DialogInterface;

import com.example.l.common.utils.DialogHelp;

/**
 * 界面帮助类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36
 * 
 */
public class UIHelper {
    /**
     * 发送App异常崩溃报告
     *
     * @param context
     * @param crashReport
     */
    public static void sendAppCrashReport(final Context context) {

        DialogHelp.getConfirmDialog(context, "程序发生异常", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 退出
                System.exit(-1);
            }
        }).show();
    }
}
