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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBefore(stories:List<Story>?)

    @Update
    suspend fun update(story: Story)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(story: List<Story>)

    @Query("Select * From Story where id=:id")
    suspend fun query(id:Long):Story?

    @Query("Select * From Story Where date=:date and isTopStory==0 order by orderNum desc")
    suspend fun queryByDate(date:Long):List<Story>?

    @Query("Select * From story where date= (Select MAX(date) From story) and isTopStory==0 order by orderNum desc ")
    suspend fun queryLatestStory(): List<Story>?

    @Query("Delete From story where date<:date")
    suspend fun deleteStory(date:Long)

    @Query("Select * From story where isLike=1 order by date desc")
    suspend fun queryLikeStory():List<Story>?
}