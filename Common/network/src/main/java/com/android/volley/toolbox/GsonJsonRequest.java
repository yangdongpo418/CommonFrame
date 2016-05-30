package com.android.volley.toolbox;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by byang059 on 5/19/2016.
 */
public class GsonJsonRequest<T> extends Request<T> {
    protected static final String PROTOCOL_CHARSET = "utf-8";
    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final Map<String, Object> bodyParam;
    private final Response.Listener<T> listener;
    private HashMap<String, String> headers;
    private Class<T> mClass;


    @Override
    public int compareTo(Request<T> other) {
        return super.compareTo(other);
    }

    public GsonJsonRequest(int method, String url, Map<String, Object> bodyParam, Class<T> clazz,Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mClass = clazz;
        this.bodyParam = bodyParam;
        this.listener = listener;
        if (method == Method.GET && bodyParam != null) {
            addGetParams();
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

    public void addGetParams() {
        if (bodyParam != null) {
            Uri uri = Uri.parse(mUrl);
            Uri.Builder builder = uri.buildUpon();
            for (HashMap.Entry<String, Object> entry : bodyParam.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
            mUrl = builder.build().toString();
        }
    }

}
