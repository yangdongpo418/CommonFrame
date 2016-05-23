package com.example.l.common.api;

import com.example.l.common.bean.Weather;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:dongpo 创建时间: 2016/5/21
 * 描述:all request Api will be listed on this class.
 * 修改:
 */
public class RequestApi {

    /**
     * 请求天气信息
     */
    public static void weatherInfo(RequestListener<Weather> listener){
        Map<String,Object> params = new HashMap<>();
        params.put("name","zhangsan");
        params.put("value","李四");
        params.put("age",35);
        HttpClient.post("http://10.0.2.2:8080/manager/volley.json",params,Weather.class,listener);
    }
}
