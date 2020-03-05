package com.cwh.mvvm_coroutines.db.dao

import androidx.room.*
import com.cwh.mvvm_coroutines.model.Story

/**
 * Description:
 * Date：2020/3/3-17:42
 * Author: cwh
 */
@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(story: Story)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(stories:List<Story>?)

    @Update
    suspend fun update(story: Story)

    @Query("Select * From Story where id=:id")
    suspend fun query(id:Long):Story?

    @Query("Select * From Story Where date=:date order by orderNum asc")
    suspend fun queryByDate(date:Long):List<Story>?

    @Query("Select * From story where date= (Select MAX(date) From story) order by orderNum asc ")
    suspend fun queryLatestStory(): List<Story>?

    @Query("Delete From story where date<:date")
    suspend fun deleteStory(date:Long)
}