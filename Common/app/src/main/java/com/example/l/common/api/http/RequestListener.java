package com.example.l.common.api.http;

import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.l.common.utils.TLog;

/**
 * Created by byang059 on 5/23/2016.
 */
public abstract class RequestListener<T> implements Response.Listener<T>,Response.ErrorListener {
    public abstract void onSuccess(T response);
    public abstract void onFailure(String error);

    public RequestListener() {
        super();
    }

    @Override
    public final void onErrorResponse(VolleyError error) {
        String err =  null;
        if (error instanceof TimeoutError){
            err = "网络超时";
        }else if (error instanceof ParseError){
            err = "解析错误";
            error.printStackTrace();
        }else if (error instanceof NetworkError){
            err = "网络异常";
        } else if (error instanceof ClientError){
            err = "客户端异常";
        }else if (error instanceof ServerError){
            err = "服务器异常";
        } else{
            err = "未知异常";
        }

        TLog.exception(error);
        TLog.i(err);
        onFailure(err);
    }

    @Override
    public final void onResponse(T response) {
        onSuccess(response);
    }
}
