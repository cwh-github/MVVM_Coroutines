package com.cwh.mvvm_coroutines.model

/**
 * Description:
 * Date：2020/3/6-14:03
 * Author: cwh
 */
data class CommentInfo(
    //总评论数
    val comments: Int,
    //长评数
    val long_comments: Int,
    //点赞数
    val popularity: Int,
    //短评数
    val short_comments: Int
)