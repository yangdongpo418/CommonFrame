package com.example.l.common.interf;

import com.example.l.common.model.entity.MovieInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author:dongpo 创建时间: 6/20/2016
 * 描述:
 * 修改:
 */
public interface MoiveService {

    @GET("top250")
    Observable<MovieInfo> getMoive(@Query("start") int start, @Query("count") int count);

}
