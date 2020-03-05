package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
import com.cwh.mvvm_coroutines.db.DataBaseHelper
import com.cwh.mvvm_coroutines.model.BeforeNews
import com.cwh.mvvm_coroutines.utils.TimeParseUtils
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
    suspend fun beforeNews(date:Long):BeforeNews
}

class RemoteBeforeNewsRepository:IBeforeNewsRepository, BaseRemoteRepository() {

    private val apiService=RetrofitUtils.createServiceInstance(NewsApiService::class.java)
    override suspend fun beforeNews(date: Long): BeforeNews {
        val result=apiService.beforeNewsData(date)
        result.stories?.forEachIndexed{index,story ->
            story.orderNum=index
            story.date=result.date
        }
        return result
    }

}

class LocalBeforeNewsRepository:IBeforeNewsRepository, BaseLocalRepository() {

    private val helper=DataBaseHelper.instance()
    override suspend fun beforeNews(date: Long): BeforeNews {
        val stories=helper.storyByDate(date)
        return BeforeNews(TimeParseUtils.beforeTime2Long(date,1),
            stories,false)
    }

}


class BeforeNewsRepository :IBeforeNewsRepository,
    BaseRepository<RemoteBeforeNewsRepository,LocalBeforeNewsRepository>(){


    override val remote: RemoteBeforeNewsRepository
        get() = RemoteBeforeNewsRepository()
    override val local: LocalBeforeNewsRepository
        get() = LocalBeforeNewsRepository()

    private val helper=DataBaseHelper.instance()
    override suspend fun beforeNews(date: Long): BeforeNews {
        val localResult=local.beforeNews(date)
        return if(localResult.stories.isNullOrEmpty()){
            val remoteResult=remote.beforeNews(date)
            helper.insertStory(remoteResult.stories)
            remoteResult
        }else{
            localResult
        }
    }
}