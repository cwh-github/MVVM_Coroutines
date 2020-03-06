package com.cwh.mvvm_coroutines.ui.main

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cwh.mvvm_coroutines.model.BeforeNews
import com.cwh.mvvm_coroutines.model.LatestNews
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.repository.BeforeNewsRepository
import com.cwh.mvvm_coroutines.model.repository.LatestNewsRepository
import com.cwh.mvvm_coroutines_base.base.Event
import com.cwh.mvvm_coroutines_base.base.Result
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingAction
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingCommand
import com.cwh.mvvm_coroutines_base.base.viewmodel.BaseViewModel
import com.cwh.mvvm_coroutines_base.utils.LogUtils

/**
 * Description:
 * Date：2020/3/4-20:14
 * Author: cwh
 */

class NewsListViewModel(application: Application) :
    BaseViewModel<LatestNewsRepository>(application) {
    override val repo: LatestNewsRepository
        get() = LatestNewsRepository()

    val otherRepo = BeforeNewsRepository()

    val latestNews = MutableLiveData<Result<LatestNews>>()

    val beforeNews=MutableLiveData<Result<BeforeNews>>()


    val command = BindingCommand(object : BindingAction {
        override fun call() {
            LogUtils.d("Command", "Call Excute")
            latestNewsList()
        }
    })


    /**
     * 获取最新story
     */
    fun latestNewsList() {
        launch(onStart = {
            latestNews.value = Result.loading(null)
        }, block = {
            val result = repo.latestNews()
            latestNews.value = Result.success(result)
        },onError = {
            latestNews.value= Result.error(null,null)
        })

    }

    /**
     * 获取之前指定日期的新闻
     */
    fun beforeNewsList(date: Long) {
        launch(
            onStart = {
                beforeNews.value= Result.loading(null)
            },
            block = {
                val result=otherRepo.beforeNews(date)
                beforeNews.value= Result.success(result)
            },onError = {
                beforeNews.value= Result.error(it.message,null)
            }

        )
    }

    /**
     * 设置story 已读
     */
    fun setStoryIsRead(story: Story){
        launchOnUI {
            repo.readStory(story)
        }
    }


}