package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
import com.cwh.mvvm_coroutines.model.CommentInfo
import com.cwh.mvvm_coroutines_base.base.net.RetrofitUtils
import com.cwh.mvvm_coroutines_base.base.repository.BaseLocalRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRemoteRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Description:
 * Date：2020/3/3-14:07
 * Author: cwh
 */
interface INewsDetailsRepository {

    /**
     * 获取新闻详情
     */
    suspend fun newsDetails(id: Long): String?

    /**
     * 获取评论信息
     */
    suspend fun commentsInfo(id:Long):CommentInfo
}

class RemoteNewsDetailsRepository : BaseRemoteRepository(), INewsDetailsRepository {


    private val apiService = RetrofitUtils.createServiceInstance(NewsApiService::class.java)
    override suspend fun newsDetails(id: Long): String? {
        //todo 需要用jsoup整理数据，存入数据库
        val response = apiService.newsDetails(id)
        return if (response.isSuccessful) {
            response.body()?.string()
        } else {
            null
        }

    }

    override suspend fun commentsInfo(id: Long): CommentInfo {
        return apiService.getNewsComments(id)
    }

}

class LocalNewsDetailsRepository : BaseLocalRepository(){

    suspend fun newsDetails(id: Long): String? {
        return null
    }

}

class NewsDetailsRepository : BaseRepository<RemoteNewsDetailsRepository,
        LocalNewsDetailsRepository>(), INewsDetailsRepository {


    override val remote: RemoteNewsDetailsRepository
        get() = RemoteNewsDetailsRepository()
    override val local: LocalNewsDetailsRepository
        get() = LocalNewsDetailsRepository()

    override suspend fun newsDetails(id: Long): String? {
        //TODO("后续需要对数据处理和存入本地数据库")
        return remote.newsDetails(id)
    }

    override suspend fun commentsInfo(id: Long): CommentInfo {
        return remote.commentsInfo(id)
    }

}