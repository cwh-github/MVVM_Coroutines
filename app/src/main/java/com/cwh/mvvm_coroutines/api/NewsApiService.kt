package com.cwh.mvvm_coroutines.api

import com.cwh.mvvm_coroutines.model.BeforeNews
import com.cwh.mvvm_coroutines.model.CommentInfo
import com.cwh.mvvm_coroutines.model.CommentList
import com.cwh.mvvm_coroutines.model.LatestNews
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

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


    /**
     *
     *  根据时间获取新闻，如获取2013 11 18号的新闻，如下地址
     *  https://news-at.zhihu.com/api/4/news/before/20131119
     *
     *  获取给定时间的前一天的新闻
     */
    @GET("news/before/{date}")
    suspend fun beforeNewsData(@Path("date")date:Long):BeforeNews

    /**
     * 获取url 对应的新闻具体数据()具体数据实际为html
     * @param id 新闻对应的具体id
     */
    @GET("https://daily.zhihu.com/story/{id}")
    suspend fun newsDetails(@Path("id")id:Long): Response<ResponseBody>

    /**
     * 获取评论数和点赞数等数据
     */
    //https://news-at.zhihu.com/api/4/story-extra/1848590
    @GET("story-extra/{id}")
    suspend fun getNewsComments(@Path("id")id:Long):CommentInfo


    /**
     * 获取长评论
     */
    //https://news-at.zhihu.com/api/4/story/8997528/long-comments
    @GET("story/{id}/long-comments")
    suspend fun getNewsLongComments(@Path("id") id :Long):CommentList

    /**
     * 获取短评论
     */
    //https://news-at.zhihu.com/api/4/story/4232852/short-comments
    @GET("story/{id}/short-comments")
    suspend fun getNewsShortComments(@Path("id") id:Long):CommentList

}