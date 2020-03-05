package com.cwh.mvvm_coroutines.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Description:
 * Date：2020/3/3-16:52
 * Author: cwh
 */
@Entity(foreignKeys = [ForeignKey(entity = Story::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("newsId"),onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)]
)
data class NewsDetails(
    /**
     * 新闻对应的id
     */
    @PrimaryKey(autoGenerate = false)
    var newsId:Long,
    /**
     * 具体类容
     */
    var content:String,
    /**
     * 头部image对应的url
     */
    var image:String,
    /**
     * title
     */
    var title:String,
    var author:String){
    constructor():this(0L,"","","","")
}