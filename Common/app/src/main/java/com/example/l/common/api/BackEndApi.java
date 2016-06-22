package com.example.l.common.api;

import com.example.l.common.interf.MoiveService;
import com.example.l.common.model.entity.MovieInfo;
import com.example.l.common.model.entity.Weather;
import com.thirdparty.proxy.net.volley.HttpUtils;
import com.thirdparty.proxy.net.volley.RequestListener;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author:dongpo 创建时间: 2016/5/21
 * 描述:all request Api will be listed on this class.
 * 修改:
 */
public class BackEndApi {

    static{
        backEndService = RetrofitClient.getInstance().getService();
    }

    private static MoiveService backEndService;

    /**
     * 请求天气信息
     */
    public static void weatherInfo(RequestListener<Weather> listener){
        Map<String,Object> params = new HashMap<>();
        params.put("name","zhangsan");
        params.put("value","李四");
        params.put("age",35);
        HttpUtils.getBean("http://10.0.2.2:8080/manager/volley.json",params,Weather.class,listener);
    }

    public static void getMovie(RequestListener<MovieInfo> listener){
        Map<String,Object> params = new HashMap<>();
        params.put("start","0");
        params.put("count","10");
        HttpUtils.getBean("https://api.douban.com/v2/movie/top250",params,MovieInfo.class,listener);
    }

    /**
     * 请求百度
     */
    public static void baiDu(RequestListener<String> listener){
        HttpUtils.getString("http://www.baidu.com",listener);
    }

    public static Subscription movieList(int start, int count, Subscriber<MovieInfo> callback){
        Observable<MovieInfo> movie = backEndService.getMoive(start, count);
        Subscription subscribe = movie.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
        return subscribe;
    }

}
