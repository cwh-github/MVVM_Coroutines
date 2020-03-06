package com.cwh.mvvm_coroutines.model

/**
 * Description:用户评论，包括短评和长评
 * Date：2020/3/6-15:29
 * Author: cwh
 */
data class CommentList(
    val comments: MutableList<Comment>
)

data class Comment(
    val author: String,
    val avatar: String,
    val content: String,
    val id: Int,
    val likes: Int,
    val reply_to: ReplyTo?,
    val time: Long,
    //是否是长评
    var isLong:Boolean,
    var isTitle:Boolean

)

data class ReplyTo(
    val author: String,
    val content: String,
    val id: Int,
    val status: Int
)