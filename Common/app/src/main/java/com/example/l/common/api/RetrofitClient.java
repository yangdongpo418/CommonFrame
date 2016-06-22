package com.example.l.common.api;

import com.example.l.common.interf.MoiveService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author:dongpo 创建时间: 6/20/2016
 * 描述:
 * 修改:
 */
public class RetrofitClient {

    private final Retrofit mRetrofit;
    private final MoiveService mMoiveService;
    private static RetrofitClient client = new RetrofitClient();

    private RetrofitClient(){
        /*mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.douban.com/v2/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();*/
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.douban.com/v2/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        mMoiveService = mRetrofit.create(MoiveService.class);
    }

    public static RetrofitClient getInstance(){
        return client;
    }

    public MoiveService getService(){
        return mMoiveService;
    }


}
