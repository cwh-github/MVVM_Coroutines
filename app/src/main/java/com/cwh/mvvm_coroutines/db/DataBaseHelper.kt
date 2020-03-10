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
     * 插入story
     */
    suspend fun insertBeforeStory(stories:List<Story>?){
        db.storyDao().insertBefore(stories)
    }

    /**
     * 更新story
     */
    suspend fun updateStory(story: Story){
        db.storyDao().update(story)
    }

    /**
     * 更新story
     */
    suspend fun updateStory(story: List<Story>){
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
     * 获取所有like story
     */
    suspend fun storyLike():List<Story>?{
        return db.storyDao().queryLikeStory()
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
     * 删除比date小的数据
     */
    suspend fun deleteStory(date:Long){
        db.storyDao().deleteStory(date)
    }

    /**
     * 获取比date小的story
     */
    suspend fun queryByLessDate(date: Long):List<Story>?{
        return db.storyDao().queryByLessDate(date)
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


    suspend fun insertDetails(newsDetails: NewsDetails){
        db.detailsDao().insert(newsDetails)
    }

    suspend fun insertDetails(newsDetails: List<NewsDetails>){
        db.detailsDao().insert(newsDetails)
    }

    suspend fun updateDetails(newsDetails: NewsDetails){
        db.detailsDao().update(newsDetails)
    }

    suspend fun queryDetailsById(newId:Long):NewsDetails?{
        return db.detailsDao().query(newId)
    }

}