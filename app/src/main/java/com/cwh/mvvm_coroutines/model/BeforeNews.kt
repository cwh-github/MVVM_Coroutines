package com.cwh.mvvm_coroutines.model

import com.cwh.mvvm_coroutines.model.Story

/**
 * Description:
 * Date：2020/3/3-12:54
 * Author: cwh
 *
 * @param date 新闻对应的时间
 */
data class BeforeNews(val date:Long,var stories: List<Story>?,val isTime:Boolean)