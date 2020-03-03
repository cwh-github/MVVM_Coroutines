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
 * Date：2020/3/3-18:49
 * Author: cwh
 */
@Database(entities = [Story::class,TopStory::class,NewsDetails::class],version = 1,exportSchema = false)
abstract class DataBase: RoomDatabase() {

    abstract fun storyDao():StoryDao

    abstract fun topStoryDao():TopStoryDao

    abstract fun detailsDao():NewsDetailsDao

}