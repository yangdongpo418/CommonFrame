package com.example.l.common.api.http;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.GsonJsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by byang059 on 5/23/2016.
 */
public class HttpUtils {

    private static Map<String, Object> publicParams;

    public static <T> void get(String url, Map<String, Object> params, Class<T> clazz, RequestListener<T> listener) {
        url = addPublicParams(url);
        GsonJsonRequest request = new GsonJsonRequest(Request.Method.GET, url, params, clazz, listener, listener);
        RequestManager.getRequestQueue().add(request);
    }

    public static <T> void post(String url, Map<String, Object> params, Class<T> clazz, RequestListener<T> listener) {
        url = addPublicParams(url);
        GsonJsonRequest request = new GsonJsonRequest(Request.Method.POST, url, params, clazz, listener, listener);
        RequestManager.getRequestQueue().add(request);
    }


    /**
     * 根据实际的业务加入公共参数，例如当前app的版本号，后台api的版本
     */
    private static String addPublicParams(String url) {
        if (publicParams != null && publicParams.size() != 0) {
            Uri uri = Uri.parse(url);
            Uri.Builder builder = uri.buildUpon();
            for (HashMap.Entry<String, Object> entry : publicParams.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
            return builder.build().toString();
        }
        return url;
    }

    public static void setPublicParams(Map<String, Object> publicParams) {
        HttpUtils.publicParams = publicParams;
    }
}
