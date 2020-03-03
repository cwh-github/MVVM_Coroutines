package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
import com.cwh.mvvm_coroutines.model.BeforeNews
import com.cwh.mvvm_coroutines_base.base.net.RetrofitUtils
import com.cwh.mvvm_coroutines_base.base.repository.BaseLocalRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRemoteRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRepository

/**
 * Description:
 * Date：2020/3/3-13:35
 * Author: cwh
 */
interface IBeforeNewsRepository{

    /**
     * 根据给定时间，获取之前news
     * @param date 给定时间 ，如：20200303
     */
    suspend fun beforNews(date:Long):BeforeNews
}

class RemoteBeforeNewsRepository:IBeforeNewsRepository, BaseRemoteRepository() {

    private val apiService=RetrofitUtils.createServiceInstance(NewsApiService::class.java)
    override suspend fun beforNews(date: Long): BeforeNews {
        return apiService.beforeNewsData(date)
    }

}

class LocalBeforeNewsRepository:IBeforeNewsRepository, BaseLocalRepository() {
    override suspend fun beforNews(date: Long): BeforeNews {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


class BeforeNewsRepository :IBeforeNewsRepository,
    BaseRepository<RemoteBeforeNewsRepository,LocalBeforeNewsRepository>(){


    override val remote: RemoteBeforeNewsRepository
        get() = RemoteBeforeNewsRepository()
    override val local: LocalBeforeNewsRepository
        get() = LocalBeforeNewsRepository()


    override suspend fun beforNews(date: Long): BeforeNews {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}