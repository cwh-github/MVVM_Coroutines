package com.cwh.mvvm_coroutines.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cwh.mvvm_coroutines.db.dao.NewsDetailsDao
import com.cwh.mvvm_coroutines.db.dao.StoryDao
import com.cwh.mvvm_coroutines.db.dao.TopStoryDao
import com.cwh.mvvm_coroutines.model.NewsDetails
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.TopStory

/**
 * Description:
 * 当使用liveData作为返回的结果数据时，不能用suspend
 * 因为使用livedata时，会自动在后台查询返回结果，不需要
 * suspend ,切刚开始范回复结果中livedata的value 是空
 * 在查询到结果后，在设置value给livedata,且在dao中只能用
 * liveData<T>不能用MutableLiveData<T>
 *      https://blog.csdn.net/yuzhiqiang_1993/article/details/101534235
 *     https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
 *
 * Date：2020/3/3-18:49
 * Author: cwh
 */
@Database(entities = [Story::class,TopStory::class,NewsDetails::class],version = 1,exportSchema = false)
abstract class DataBase: RoomDatabase() {

    abstract fun storyDao():StoryDao

    abstract fun topStoryDao():TopStoryDao

    abstract fun detailsDao():NewsDetailsDao

}