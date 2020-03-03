package com.cwh.mvvm_coroutines.db.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.cwh.mvvm_coroutines.model.NewsDetails

/**
 * Description:
 * Date：2020/3/3-18:35
 * Author: cwh
 */
@Dao
interface NewsDetailsDao {


    @Query("Select * From NewsDetails Where newsId=:newsId")
    suspend fun query(newsId:Long):MutableLiveData<NewsDetails?>

    @Update(onConflict = OnConflictStrategy.REPLACE )
    suspend fun update(newsDetails: NewsDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newsDetails: NewsDetails)
}