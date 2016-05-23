package com.android.volley.base;

/**
 * Created by byang059 on 5/19/2016.
 * 实际业务需要加入的公共参数
 */
public class PublicParams {
    private PublicParams(){}
    private static PublicParams publicParams = new PublicParams();

    public static PublicParams instance(){
        return publicParams;
    }

    public String appVersion = "123";
    public String apiVersion = "456";

    @Override
    public String toString() {
        return new StringBuffer().append("appVersion").append("=").append(appVersion)
                .append("&").append("apiVersion").append("=").append(apiVersion).toString();
    }
}
