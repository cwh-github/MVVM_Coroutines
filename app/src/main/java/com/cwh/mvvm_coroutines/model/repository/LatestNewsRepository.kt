package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
import com.cwh.mvvm_coroutines.db.DataBaseHelper
import com.cwh.mvvm_coroutines.model.LatestNews
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.TopStory
import com.cwh.mvvm_coroutines.utils.TimeParseUtils
import com.cwh.mvvm_coroutines_base.base.net.RetrofitUtils
import com.cwh.mvvm_coroutines_base.base.repository.BaseLocalRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRemoteRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRepository
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import java.lang.Exception

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
        val result=apiService.latestNews()
        result.stories?.forEachIndexed{index,story ->
            story.orderNum=result.stories!!.size-index
            story.date=result.date
        }
        return result
    }

}

//从数据库获取数据
class LocalLatestRepository:BaseLocalRepository(),ILatestNewsRepository{
    private val dbHelper=DataBaseHelper.instance()
    override suspend fun latestNews(): LatestNews {
        val stories=dbHelper.storyLatest()
        val topStories=dbHelper.queryTopStory()
        return LatestNews(TimeParseUtils.currentTime2Long(),stories,topStories)
    }

}

//获取最新数据
class LatestNewsRepository:BaseRepository<RemoteLatestRepository,LocalLatestRepository>(),ILatestNewsRepository{
    override val remote: RemoteLatestRepository
        get() = RemoteLatestRepository()
    override val local: LocalLatestRepository
        get() = LocalLatestRepository()

    private val dbHelper=DataBaseHelper.instance()

    override suspend fun latestNews(): LatestNews {
        var result:LatestNews
        try {
            //从服务器加载失败，加载数据库中数据
            result=remote.latestNews()
            //实际应该在ViewModel中存储最好
            dbHelper.insertStory(result.stories)
            val topStory=result.top_stories
            topStory?.let {
                val result=formatToStory(it,result.date)
                dbHelper.insertStory(result)
            }
            dbHelper.deleteTopStory()
            dbHelper.insertTopStory(result.top_stories)
        }catch (e:Exception){
            e.printStackTrace()
            LogUtils.e("Error is ${e.message}")
        }
        return local.latestNews()
    }

    /**
     * 将topstory转为story,插入story表，应为在重头部Image进入时
     * 由于可能story中没有，造成外键构造失败
     */
    private fun formatToStory(
        list: List<TopStory>,
        date: Long
    ): MutableList<Story> {
        val data= mutableListOf<Story>()
        list.forEach {
            val story=Story(it.ga_prefix,it.hint!!,it.id.toLong(),it.image_hue!!, arrayListOf(it.image),it.title,
                it.type,it.url,isLike = false,isRead = false,date = date,isTime = false,orderNum = 0,
                isTopStory = true)
            data.add(story)
        }
        return data
    }

    //设置story 已读
    suspend fun readStory(story:Story){
        story.isRead=true
        dbHelper.updateStory(story)
    }

}