package com.cwh.mvvm_coroutines.db.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.cwh.mvvm_coroutines.model.Story

/**
 * Description:
 * Date：2020/3/3-17:42
 * Author: cwh
 */
@Dao
interface StoryDao {

    @Insert
    suspend fun insert(story: Story)

    @Insert
    suspend fun insert(stories:List<Story>)

    @Update
    suspend fun update(story: Story)

    @Query("Select * From Story where id=:id")
    suspend fun query(id:Long):MutableLiveData<Story?>

    @Query("Select * From Story Where date=:date order by id desc")
    suspend fun queryByDate(date:Long):MutableLiveData<List<Story>?>

    @Query("Select * From story where date= (Select MAX(date) From story) order by id desc ")
    suspend fun queryLatestStory():MutableLiveData<List<Story>?>

    @Query("Delete From story where date<:date")
    suspend fun deleteStory(date:Long)
}