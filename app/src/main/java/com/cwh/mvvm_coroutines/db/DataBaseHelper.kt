package com.cwh.mvvm_coroutines.db

import androidx.room.Room
import com.cwh.mvvm_coroutines.model.NewsDetails
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.TopStory
import com.cwh.mvvm_coroutines.utils.TimeParseUtils
import com.cwh.mvvm_coroutines_base.utils.ConUtils

/**
 * Description:
 * Date：2020/3/3-18:51
 * Author: cwh
 */
class DataBaseHelper private constructor(){

    private val db=Room.databaseBuilder(ConUtils.mContext(),
        DataBase::class.java, DB_NAME)
        .build()

    companion object{
        const val DB_NAME="story"

        private var instance:DataBaseHelper?=null

        fun instance():DataBaseHelper{
            return instance?: synchronized(this){
                instance?:DataBaseHelper().also {
                    instance=it
                }
            }

        }

    }

    /**
     * 插入story
     */
    suspend fun insertStory(stories:List<Story>?){
        db.storyDao().insert(stories)
    }

    /**
     * 更新story
     */
    suspend fun updateStory(story: Story){
        db.storyDao().update(story)
    }

    /**
     * 设置story为已读
     */
    suspend fun storyIsRead(story: Story){
        story.isRead=true
        updateStory(story)
    }

    /**
     * 设置是否已收藏
     */
    suspend fun storyLike(isLike:Boolean,story: Story){
        story.isLike=isLike
        updateStory(story)
    }

    /**
     * 根据时间获取stories
     */
    suspend fun storyByDate(date:Long): List<Story>? {
        val newDate=TimeParseUtils.beforeTime2Long(date,1)
        return db.storyDao().queryByDate(newDate)
    }

    /**
     * 获取最近新闻
     */
    suspend fun storyLatest():List<Story>?{
        return  db.storyDao().queryLatestStory()
    }

    /**
     * 根据id查询story
     */
    suspend fun storyById(id:Long):Story?{
        return db.storyDao().query(id)
    }

    /**
     * 删除对应的story
     * 保留现在开始二十天之前数据
     */
    suspend fun deleteStory(){
        val currentDate=TimeParseUtils.currentTime2Long()
        db.storyDao().deleteStory(TimeParseUtils.beforeTime2Long(currentDate,20))
    }

    /**
     * 查询获取所有的Topstories
     */
    suspend fun queryTopStory():List<TopStory>?{
        return db.topStoryDao().query()
    }

    /**
     * 删除所有的TopStories
     */
    suspend fun deleteTopStory(){
        db.topStoryDao().delete()
    }

    /**
     * 插入所有的Topstories
     */
    suspend fun insertTopStory(stories: List<TopStory>?){
        db.topStoryDao().insert(stories)
    }


    suspend fun insertDetailsS(newsDetails: NewsDetails){
        db.detailsDao().insert(newsDetails)
    }

    suspend fun updateDetails(newsDetails: NewsDetails){
        db.detailsDao().update(newsDetails)
    }

    suspend fun queryDetailsById(newId:Long):NewsDetails?{
        return db.detailsDao().query(newId)
    }

}