package com.android.volley.base;

import android.os.Debug;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by byang059 on 5/19/2016.
 * This is common custom request, always used for normal busniess work, it include below function
 * 1.进一步封装更接近实际业务的request
 * 2.用户输入内部类的形式，类似key value，我内部使用Gson进行封装。
 * 3.提供公共参数接口 get方式或者post方式
 * 4.
 */
public class GsonJsonRequest<T> extends Request<T> {
    protected static final String PROTOCOL_CHARSET = "utf-8";
    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final ResponseCallBack<T> listener;
    private final BodyParam bodyParam;
    private HashMap<String, String> headers;
    public boolean isAddPublicParam = false;
    public Class<T> mClass;

    @Override
    public int compareTo(Request<T> other) {
        return super.compareTo(other);
    }

    public GsonJsonRequest(int method, String url, BodyParam bodyParam, ResponseCallBack listener, boolean isAddPublicParam) {
        super(method, url, listener);
        this.listener = listener;
        this.bodyParam = bodyParam;
        this.isAddPublicParam = isAddPublicParam;
        setShouldCache(false);

        if (method == Method.GET && bodyParam != null) {
            addGetParams();
        }

        if (isAddPublicParam) {
            addPublicParams();
        }
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(GsonUtils.fromJson(jsonString, mClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * @return 只在post put patch请求方法中调用
     * @throws AuthFailureError
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return bodyParam == null ? null : GsonUtils.toJson(bodyParam).getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    bodyParam, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }


    /**
     * @return 在上面getBody函数没有被重写情况下，此方法的返回值会被 key、value 分别编码后拼装起来转换为字节码作为 Body 内容
     * @throws AuthFailureError
     */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }

    /**
     * 决定请求体的数据格式
     *
     * @return
     */
    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    /**
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers == null) {
            return super.getHeaders();
        }
        return headers;
    }

    /**
     * 往一个request中加入一个header
     */
    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }

    /**
     * 根据实际的业务加入公共参数，例如当前app的版本号，后台api的版本
     */
    public void addPublicParams() {
        StringBuilder sb = new StringBuilder();
        if (mMethod == Method.GET) {
            if (bodyParam == null) {
                mUrl = sb.append(mUrl).append("?").append(PublicParams.instance().toString()).toString();
            } else {
                mUrl = sb.append(mUrl).append("&").append(PublicParams.instance().toString()).toString();
            }
        } else if (mMethod == Method.POST) {
            mUrl = sb.append(mUrl).append("?").append(PublicParams.instance().toString()).toString();
        }
    }

    public void addGetParams(){
        StringBuilder sb = new StringBuilder();
        sb.append(mUrl);
        sb.append("?");

        Class<?> clazz = bodyParam.getClass();
        Field[] fields = clazz.getFields();

        if (fields.length == 0){
            return;
        }

        for (int i = 0 ; i < fields.length ; i++ ) {
            Field field =  fields[i];
            String name = field.getName();
            String value = null;
            try {
                Object v = field.get(bodyParam);
                if (v == null){
                    continue;
                }
                value = String.valueOf(v);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (i == fields.length -1){
                sb.append(name).append("=").append(value);
            }else{
                sb.append(name).append("=").append(value).append("&");
            }
        }

        mUrl = sb.toString();
    }
}
