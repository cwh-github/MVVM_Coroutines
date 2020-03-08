package com.cwh.mvvm_coroutines.ui.like

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.repository.CollectionRepository
import com.cwh.mvvm_coroutines_base.base.Result
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingAction
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingCommand
import com.cwh.mvvm_coroutines_base.base.viewmodel.BaseViewModel

/**
 * Description:
 * Date：2020/3/7-13:05
 * Author: cwh
 */

class LikeStoryViewModel(application: Application) :
    BaseViewModel<CollectionRepository>(application) {
    override val repo: CollectionRepository
        get() = CollectionRepository()


    val mLikeStories=MutableLiveData<Result<List<Story>>>()

    /**
     * 获取收藏story
     */
    fun likeStoryList(){
        launch(
            onStart = {
                mLikeStories.value= Result.loading(null)
            },
            block = {
                val result=repo.likeStoryList()
                mLikeStories.value= Result.success(result)
            },
            onError = {
                mLikeStories.value= Result.error()
            }
        )
    }


    val command=BindingCommand(object :BindingAction{
        override fun call() {
            likeStoryList()
        }

    })


}