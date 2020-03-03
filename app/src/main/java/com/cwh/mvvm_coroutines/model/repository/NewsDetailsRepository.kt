package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
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

}

class LocalNewsDetailsRepository : BaseLocalRepository(), INewsDetailsRepository {
    override suspend fun newsDetails(id: Long): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class NewsDetailsRepository : BaseRepository<RemoteNewsDetailsRepository,
        LocalNewsDetailsRepository>(), INewsDetailsRepository {
    override val remote: RemoteNewsDetailsRepository
        get() = RemoteNewsDetailsRepository()
    override val local: LocalNewsDetailsRepository
        get() = LocalNewsDetailsRepository()

    override suspend fun newsDetails(id: Long): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}