package com.example.l.common.api.http;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.l.common.application.AppContext;

/**
 * Created by byang059 on 5/23/2016.
 */
public class RequestManager {
    private RequestManager(){}

    private static RequestQueue queue  = Volley.newRequestQueue(AppContext.getInstance());

    public static RequestQueue getRequestQueue(){
        return queue;
    }
}
