package com.cwh.mvvm_coroutines.api

import com.cwh.mvvm_coroutines.model.LatestNews
import retrofit2.http.GET

/**
 * Description:
 * Date：2020/3/2-13:32
 * Author: cwh
 */

interface NewsApiService{


    /**
     * 获取最新的数据，包括顶层图片和最新数据列表
     */
    @GET("news/latest")
    suspend fun latestNews():LatestNews

}