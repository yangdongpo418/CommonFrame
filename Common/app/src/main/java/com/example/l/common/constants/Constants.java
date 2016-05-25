package com.example.l.common.constants;

/**
 * 常量类
 * 
 * @author
 * @version
 * 
 */

public class Constants {

    public static final String INTENT_ACTION_USER_CHANGE = "Action_USER_CHANGE";

    //这个广播是注销登陆会发送的
    public static final String INTENT_ACTION_LOGOUT = "Action.LOGOUT";
    public static final String INTENT_ACTION_LOGIN = "Action.LOGIN";

    /**------------网络请求的状态码，用来更新界面----------------*/
    public static final int STATE_SUCCESS = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_EMPTY = 3;
}
