package com.cwh.mvvm_coroutines.down

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.os.Environment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cwh.mvvm_coroutines.db.DataBaseHelper
import com.cwh.mvvm_coroutines.model.LatestNews
import com.cwh.mvvm_coroutines.model.NewsDetails
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.TopStory
import com.cwh.mvvm_coroutines.model.repository.BeforeNewsRepository
import com.cwh.mvvm_coroutines.model.repository.LatestNewsRepository
import com.cwh.mvvm_coroutines.model.repository.NewsDetailsRepository
import com.cwh.mvvm_coroutines.utils.GlideUtils
import com.cwh.mvvm_coroutines.utils.TimeParseUtils
import com.cwh.mvvm_coroutines_base.utils.FileUtils
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import com.cwh.mvvm_coroutines_base.utils.MD5Utils
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.File
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "com.cwh.mvvm_coroutines.down.action.FOO"
private const val ACTION_BAZ = "com.cwh.mvvm_coroutines.down.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.cwh.mvvm_coroutines.down.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.cwh.mvvm_coroutines.down.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class DownloadStoriesService : IntentService("DownloadStoriesService"),CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob()

    val latestRepo=LatestNewsRepository()

    val beforeRepo=BeforeNewsRepository()

    val detailsRepo=NewsDetailsRepository()

    /**
     * 默认下载前十五天的数据
     */
    private val BEFORE_DAYS=15

    val topStories= mutableListOf<TopStory>()
    val stories= mutableListOf<Story>()
    val detailsList= mutableListOf<NewsDetails>()
    var mTitleImageCount:Int=0
    //上次发送广播的时间
    var mLastSendTime:Long=0

    var mTitleDownloadCount: Int=0

    private val mTimeSpace=300

    override fun onHandleIntent(intent: Intent?) {
        val intent=Intent()
        intent.action= DOWN_LOAD_START
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        downloadStory()
    }


    /**
     * 请求所有需要下载的story的文字并存入数据库
     *
     */
    private  fun downloadStory()=launch(coroutineContext) {
        var latestNews:LatestNews?=null
        try {
           latestNews=latestRepo.latestNews()
        }catch (e:Exception){
            LogUtils.e("Error","下载最新新闻出错 ${e.message}")
        }
        latestNews?.top_stories?.let {
            topStories.addAll(it)
        }
        latestNews?.stories?.let {
            stories.addAll(it)
        }
        val date=latestNews?.date
        for(i in 0 until BEFORE_DAYS){
            try {
                val beforeNews = beforeRepo.beforeNews(
                    TimeParseUtils.beforeTime2Long(date ?: TimeParseUtils.currentTime2Long(), i)
                )
                beforeNews.stories?.let {
                    stories.addAll(it)
                }
                //避免请求过快，被屏蔽
                delay(200)
            }catch (e:Exception){
                LogUtils.e("Error","下载最近第${i+1}天出错 ${e.message}")
            }
        }
        mTitleImageCount=topStories.size+stories.size
        downloadDetails()
        downloadImage()
    }


    /**
     * 下载并格式化新闻详情，并存入数据库
     */
    private suspend fun downloadDetails(){
        val size=stories.size
        var count=0
        for(story in stories){
            count++
            try {
                val details = detailsRepo.downloadDetails(story.id)
                details?.let {
                    detailsList.add(it)
                }
                //避免请求过快，被屏蔽
                delay(200)
            }catch (e:Exception){
                LogUtils.e("Error","下载新闻详情出错 ${e.message}")
            }
            if(System.currentTimeMillis()-mLastSendTime>mTimeSpace){
                mLastSendTime=System.currentTimeMillis()
                val intent=Intent().setAction(DOWN_LOAD_PROGRESS)
                    .putExtra(PROGRESS,(count*10/(size)))
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        }
    }



    private fun createTargetFile(name:String):File{
        val dir=this@DownloadStoriesService.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val targetFile=File(dir,name)
        if(!targetFile.parentFile.exists()){
            targetFile.parentFile.mkdirs()
        }
        return targetFile
    }

    private suspend fun downloadImage(){
        downloadTopStoryImage()
        downloadStoriesImage()
        downloadDetailsImage()
    }

    private suspend fun downloadTopStoryImage() {
        mTitleDownloadCount=0
        for (top in topStories) {
            try {
                val file = GlideUtils.downFile(this@DownloadStoriesService, top.image)
                val targetFile = createTargetFile(MD5Utils.md5(top.image))
                FileUtils.copyFile(file, targetFile)
                top.image = targetFile.absolutePath
                mTitleDownloadCount++
            } catch (e: Exception) {
                LogUtils.e("Error", "下载Top图片失败：${top.image}+\n+" +
                        "${e.message}")
                mTitleDownloadCount++
            }
            if(System.currentTimeMillis()-mLastSendTime>mTimeSpace){
                val progress=(mTitleDownloadCount*10/mTitleImageCount)+10
                mLastSendTime=System.currentTimeMillis()
                val intent=Intent().setAction(DOWN_LOAD_PROGRESS)
                    .putExtra(PROGRESS,progress)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }

        }
        DataBaseHelper.instance().insertTopStory(topStories)
    }

    private suspend fun downloadStoriesImage(){
        for (story in stories) {
            try {
                story.images?.get(0)?.let {
                    val file = GlideUtils.downFile(this@DownloadStoriesService, it)
                    val targetFile = createTargetFile(MD5Utils.md5(it))
                    FileUtils.copyFile(file, targetFile)
                    story.images!![0] = targetFile.absolutePath
                }
                mTitleDownloadCount++
            } catch (e: Exception) {
                LogUtils.e("Error", "下载Story图片失败：${story.images?.get(0)}+\n+" +
                        "${e.message}")
                mTitleDownloadCount++
            }
            if(System.currentTimeMillis()-mLastSendTime>mTimeSpace){
                mLastSendTime=System.currentTimeMillis()
                val progress=(mTitleDownloadCount*10/mTitleImageCount)+10
                mLastSendTime=System.currentTimeMillis()
                val intent=Intent().setAction(DOWN_LOAD_PROGRESS)
                    .putExtra(PROGRESS,progress)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        }
        DataBaseHelper.instance().updateStory(stories)
    }

    private suspend fun downloadDetailsImage(){
        val size=detailsList.size
        var downloadSize=0
        for(details in detailsList){
            downloadSize++
            try{
                val url=details.image
                val file = GlideUtils.downFile(this@DownloadStoriesService, url)
                val targetFile = createTargetFile(MD5Utils.md5(url))
                FileUtils.copyFile(file, targetFile)
                details.image=targetFile.absolutePath
            }catch (e:Exception){
                LogUtils.e("Error", "下载Details Head图片失败：${details.image}+\n+" +
                        "${e.message}")
            }
            val content=details.content
            val doc=Jsoup.parse(content)
            val images=doc.select("img.content-image")
            for(element in images){
                try {
                    val url=element.attr("src")
                    val file = GlideUtils.downFile(this@DownloadStoriesService, url)
                    val targetFile = createTargetFile(MD5Utils.md5(url))
                    FileUtils.copyFile(file, targetFile)
                    element.attr("src",targetFile.absolutePath)
                }catch (e:Exception){
                    LogUtils.e("Error", "下载Details图片失败：${element.attr("src")} +\n+" +
                            "${e.message}")
                }

                if(System.currentTimeMillis()-mLastSendTime>mTimeSpace){
                    mLastSendTime=System.currentTimeMillis()
                    val intent=Intent().setAction(DOWN_LOAD_PROGRESS)
                        .putExtra(PROGRESS,(downloadSize*80/size)+20)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }
            }
        }

        val intent=Intent().setAction(DOWN_LOAD_COMPLETE)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        DataBaseHelper.instance().insertDetails(detailsList)
    }

    companion object {

        const val DOWN_LOAD_START="download.start"

        const val DOWN_LOAD_PROGRESS="download.progress"

        const val DOWN_LOAD_COMPLETE="download.complete"

        const val PROGRESS="progress"

        @JvmStatic
        fun startService(context: Context) {
            val intent = Intent(context, DownloadStoriesService::class.java)
            context.startService(intent)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

}
