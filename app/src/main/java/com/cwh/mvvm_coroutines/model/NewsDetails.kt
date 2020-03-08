package com.cwh.mvvm_coroutines.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Description:
 * Date：2020/3/3-16:52
 * Author: cwh
 */
@Entity(foreignKeys = [ForeignKey(entity = Story::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("detailsId"),onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)]
,indices = [Index(value = arrayOf("detailsId"),name="",unique = false)]
)
data class NewsDetails(
    /**
     * 新闻对应的id
     */
    @PrimaryKey(autoGenerate = false)
    var newsId:Long,

    var detailsId:Long,
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
    var author:String,
    /**
     * 作者头像
     */
    var authorImage:String?,
    /**
     * 问题Title
     */
    var questionTitle:String?,
    /**
     * 头部头像tips
     */
    var imageTips:String?

){
    constructor():this(0L,0L,"","","","","","","")
}