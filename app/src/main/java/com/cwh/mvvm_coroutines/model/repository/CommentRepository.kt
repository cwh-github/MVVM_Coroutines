package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
import com.cwh.mvvm_coroutines.model.Comment
import com.cwh.mvvm_coroutines.model.CommentList
import com.cwh.mvvm_coroutines_base.base.net.RetrofitUtils
import com.cwh.mvvm_coroutines_base.base.repository.BaseRemoteRepository

/**
 * Description:
 * Date：2020/3/6-16:42
 * Author: cwh
 */

class CommentRepository:BaseRemoteRepository(){

    private val apiService=RetrofitUtils.createServiceInstance(NewsApiService::class.java)
    //获取评论
    suspend fun comments(id:Long):MutableList<Comment>{
        val longComment=apiService.getNewsLongComments(id)
        val shortComment=apiService.getNewsShortComments(id)
        val result= mutableListOf<Comment>()
        longComment.comments.forEach {
            it.isLong=true
            result.add(it)
        }
        result.addAll(shortComment.comments)
        return result
    }

}