package com.cwh.mvvm_coroutines.db.dao

import androidx.room.*
import com.cwh.mvvm_coroutines.model.TopStory

/**
 * Description:
 * Date：2020/3/3-18:29
 * Author: cwh
 */
@Dao
interface TopStoryDao{

    @Query("Select * From TopStory order by id desc")
    suspend fun query(): List<TopStory>?

    @Query("Delete From TopStory")
    suspend fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stories: List<TopStory>?)
}