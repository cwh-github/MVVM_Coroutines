package com.cwh.mvvm_coroutines.model.repository

import com.cwh.mvvm_coroutines.db.DataBaseHelper
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines_base.base.repository.IRepository

/**
 * Description:
 * Date：2020/3/7-13:00
 * Author: cwh
 */
class CollectionRepository :IRepository{


    private val helper=DataBaseHelper.instance()

    /**
     * 获取收藏列表(后续要支持分页)
     */
    suspend fun likeStoryList():List<Story>?{
        return helper.storyLike()
    }


    override fun onClear() {

    }

}