package com.cwh.mvvm_coroutines.service

import android.app.IntentService
import android.content.Intent
import android.content.Context
import com.cwh.mvvm_coroutines.db.DataBaseHelper
import com.cwh.mvvm_coroutines.utils.SPUtils
import com.cwh.mvvm_coroutines.utils.TimeParseUtils
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.File
import java.lang.Exception
import kotlin.coroutines.CoroutineContext



/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * helper methods.
 */
class CleanCacheService : IntentService("CleanCacheIntentService"),CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = SupervisorJob()

    //最近时间n天之后的所有数据
    private val DAY_SPACE=15

    private val mDbHelper=DataBaseHelper.instance()

    override fun onHandleIntent(intent: Intent?) {
        clean()
    }

    /**
     *
     * 清理缓存的文件
     */
    private suspend  fun cleanFiles(needDelImages: MutableList<String>) {
        for (image in needDelImages){
            val file=File(image)
            if(file.exists()){
                file.deleteRecursively()
            }
        }
    }

    /**
     * 清理数据库中缓存数据
     *
     */
    private fun clean()=launch(coroutineContext){
        val lastStory=mDbHelper.storyLatest()
        var latestDate=TimeParseUtils.currentTime2Long()
        if(lastStory?.size?:0>0){
            latestDate=lastStory!![0].date
        }
        val deleteStartDate=TimeParseUtils.beforeTime2Long(latestDate,DAY_SPACE)
        val needDelStory=mDbHelper.queryByLessDate(deleteStartDate)
        LogUtils.d("Need Del","Need Del size ${needDelStory?.size}")
        val needDelImages= mutableListOf<String>()
        needDelStory?.let {
            for(story in needDelStory){
                try {
                    val images=story.images
                    if(images?.size?:0>0){
                        needDelImages.add(images!![0])
                    }
                    val detailsStory=mDbHelper.queryDetailsById(story.id)
                    detailsStory?.let {
                        needDelImages.add(it.image)
                        val doc=Jsoup.parse(it.content)
                        val images=doc.select("img.content-image")
                        for(img in images){
                            var path=img.attr("src")
                            if(!path.startsWith("http")){
                                if(path.startsWith("file://")){
                                    path=path.replace("file://","")
                                }
                                needDelImages.add(path)
                            }
                        }
                    }
                }catch (e:Exception){
                    LogUtils.e("Error","Error for clean : ${e.message}")
                }

            }
        }
        cleanFiles(needDelImages)
        LogUtils.d("Del Need","Need Del content ${needDelImages.size}")
        mDbHelper.deleteStory(deleteStartDate)
        SPUtils.saveCleanTime()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * 开启清理缓存服务
         *
         * @see IntentService
         */

        @JvmStatic
        fun startCleanService(context: Context) {
            val intent = Intent(context, CleanCacheService::class.java)
            context.startService(intent)
        }



    }
}
