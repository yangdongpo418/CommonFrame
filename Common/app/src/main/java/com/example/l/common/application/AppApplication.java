package com.example.l.common.application;

import com.example.l.common.model.entity.User;
import com.thirdparty.proxy.base.BaseApplication;



/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @author
 * @version
 * @created
 */
public class AppApplication extends BaseApplication {

    private int loginUid;
    private boolean login;

    @Override
    public void onCreate() {
        super.onCreate();
        initLogin();
    }

    private void initLogin() {
        User user = getLoginUser();
        if (null != user && user.id > 0) {
            login = true;
            loginUid = user.id;
        } else {
            this.cleanLoginInfo();
        }
    }

    /**
     * 保存登录信息
     *
     * @param user 用户信息
     */
    public void saveUserInfo(final User user) {

    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    public void updateUserInfo(final User user) {

    }

    /**
     * 获得登录用户的信息
     *
     * @return
     */
    public User getLoginUser() {
        return null;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {

    }


    public boolean isLogin() {
        return getLoginUser() == null;
    }

    /**
     * 用户注销
     */
    public void Logout() {

    }
}
