package com.thirdparty.proxy.net.volley;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.thirdparty.proxy.base.BaseApplication;

/**
 * Created by byang059 on 5/23/2016.
 */
public class RequestManager {
    private RequestManager(){}

    private static RequestQueue queue  = Volley.newRequestQueue(BaseApplication.getInstance());

    public static RequestQueue getRequestQueue(){
        return queue;
    }
}
