package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
import com.cwh.mvvm_coroutines.model.LatestNews
import com.cwh.mvvm_coroutines_base.base.net.RetrofitUtils
import com.cwh.mvvm_coroutines_base.base.repository.BaseLocalRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRemoteRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRepository

/**
 * Description:
 * Date：2020/3/2-13:45
 * Author: cwh
 */

interface ILatestNewsRepository{

   suspend fun latestNews():LatestNews
}

//从网络获取数据
class RemoteLatestRepository:BaseRemoteRepository(),ILatestNewsRepository{

    private val apiService=RetrofitUtils.createServiceInstance(NewsApiService::class.java)
    override suspend fun latestNews(): LatestNews {
        return apiService.latestNews()
    }

}

//从数据库获取数据
class LocalLatestRepository:BaseLocalRepository(),ILatestNewsRepository{
    override suspend fun latestNews(): LatestNews {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

//获取最新数据
class LatestNewsRepository:BaseRepository<RemoteLatestRepository,LocalLatestRepository>(),ILatestNewsRepository{
    override val remote: RemoteLatestRepository
        get() = RemoteLatestRepository()
    override val local: LocalLatestRepository
        get() = LocalLatestRepository()

    override suspend fun latestNews(): LatestNews {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}