package com.android.volley.base;

import com.android.volley.Response;
import com.android.volley.VolleyError;


/**
 * Created by byang059 on 5/19/2016.
 */
public interface ResponseCallBack<T> extends Response.Listener<T>,Response.ErrorListener {

    @Override
    void onErrorResponse(VolleyError error) ;

    @Override
    void onResponse(T response);

}
