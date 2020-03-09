package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.api.NewsApiService
import com.cwh.mvvm_coroutines.db.DataBaseHelper
import com.cwh.mvvm_coroutines.model.CommentInfo
import com.cwh.mvvm_coroutines.model.NewsDetails
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines_base.base.net.RetrofitUtils
import com.cwh.mvvm_coroutines_base.base.repository.BaseLocalRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRemoteRepository
import com.cwh.mvvm_coroutines_base.base.repository.BaseRepository
import com.cwh.mvvm_coroutines_base.utils.ConUtils
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.InputStreamReader

/**
 * Description:
 * Date：2020/3/3-14:07
 * Author: cwh
 */
interface INewsDetailsRepository {

    /**
     * 获取新闻详情
     */
    suspend fun newsDetails(id: Long): NewsDetails?

    /**
     * 获取评论信息
     */
    suspend fun commentsInfo(id:Long):CommentInfo
}

class RemoteNewsDetailsRepository : BaseRemoteRepository(), INewsDetailsRepository {


    private val apiService = RetrofitUtils.createServiceInstance(NewsApiService::class.java)

    private val helper=DataBaseHelper.instance()

    /**
     * 网络获取新闻详情
     */
    override suspend fun newsDetails(id: Long): NewsDetails? {
        val response = apiService.newsDetails(id)
        return if (response.isSuccessful) {
            val response=response.body()?.string()
            var newsDetails:NewsDetails?=null
            response?.let {
                withContext(Dispatchers.IO){
                    val doc=Jsoup.parse(it)
                    val header=doc.select("header.DailyHeader")
                    val headImage=header
                        .select("img").attr("src")
                    val title=header.select("p.DailyHeader-title").text()
                    val author=doc.select("span.author").first().text()
                    val authorImage=doc.select("img.avatar").first().attr("src")
                    val questionTitle=doc.select("h2.question-title").first().text()
                    val imageTips=header.select("p.DailyHeader-imageSource").first().text()
                    doc.select("h2.question-title").first().remove()
                    doc.select("div.meta").first().remove()
                    val content=doc.select("div.content-inner").first().html()
                    val html=readTempFile()
                    val result=html.replace("{content}",content)
                    newsDetails =NewsDetails(id,id,result?:"",headImage,title,author,authorImage,questionTitle,imageTips)
                    helper.insertDetails(newsDetails!!)
                    newsDetails
                }
            }
            newsDetails
        } else {
            null
        }

    }

    override suspend fun commentsInfo(id: Long): CommentInfo {
        return apiService.getNewsComments(id)
    }

    /**
     * 读取temp文件
     */
    private fun readTempFile():String{
        val buffer= StringBuffer()
        val assets=ConUtils.mContext().assets
        assets.open("template.html").use {
            InputStreamReader(it).use {
                it.readLines().forEach {str->
                    buffer.append(str)
                }
            }
        }
        return buffer.toString()
    }

}

class LocalNewsDetailsRepository : BaseLocalRepository(){

    private val helper=DataBaseHelper.instance()
    suspend fun newsDetails(id: Long): NewsDetails? {
        return helper.queryDetailsById(id)

    }

    /**
     * 收藏story
     */
    suspend fun likeOrUnLikeStory(id:Long):Boolean{
        val story=helper.storyById(id)
        var result:Boolean=false
        story?.let {
            it.isLike=!it.isLike
            helper.updateStory(it)
            result=it.isLike
        }
        return result
    }

    suspend fun storyById(id:Long):Story?{
        return helper.storyById(id)
    }

}

class NewsDetailsRepository : BaseRepository<RemoteNewsDetailsRepository,
        LocalNewsDetailsRepository>(), INewsDetailsRepository {


    override val remote: RemoteNewsDetailsRepository
        get() = RemoteNewsDetailsRepository()
    override val local: LocalNewsDetailsRepository
        get() = LocalNewsDetailsRepository()

    private val helper=DataBaseHelper.instance()

    override suspend fun newsDetails(id: Long): NewsDetails? {
        val content=local.newsDetails(id)
        if(content==null || content?.content.isNullOrEmpty()){
            return remote.newsDetails(id)
        }else{
            return content
        }
    }

    suspend fun downloadDetails(id:Long):NewsDetails?{
        var details:NewsDetails?=null
        try {
            details=remote.newsDetails(id)
        }catch (e:Exception){
            LogUtils.d("Error","Download Details Error :${e.message}")
        }
        if(details==null){
            details=local.newsDetails(id)
        }
        return details

    }

    override suspend fun commentsInfo(id: Long): CommentInfo {
        return remote.commentsInfo(id)
    }

    /**
     * 收藏story
     */
    suspend fun likeOrUnLikeStory(id: Long):Boolean{
        return local.likeOrUnLikeStory(id)
    }

    suspend fun storyById(id:Long):Story?{
        return local.storyById(id)
    }


    //设置story 已读
    suspend fun readStory(story:Story){
        story.isRead=true
        helper.updateStory(story)
    }

}