package com.cwh.mvvm_coroutines.ui.details

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cwh.mvvm_coroutines.model.CommentInfo
import com.cwh.mvvm_coroutines.model.repository.NewsDetailsRepository
import com.cwh.mvvm_coroutines_base.base.ExceptionHandle
import com.cwh.mvvm_coroutines_base.base.viewmodel.BaseViewModel

/**
 * Description:
 * Date：2020/3/6-15:44
 * Author: cwh
 */
class StoryDetailsViewModel(application: Application) :
    BaseViewModel<NewsDetailsRepository>(application) {
    override val repo: NewsDetailsRepository
        get() = NewsDetailsRepository()

    //评论数目
    val commentCount=MutableLiveData<Int>()

    val mCommentInfo=MutableLiveData<CommentInfo>()

    //点赞数目
    val likeCount=MutableLiveData<Int>()

    val toastMsg=MutableLiveData<ExceptionHandle.ResponseThrowable>()

    /**
     * 获取评论信息
     */
    fun commentInfo(id:Long){
        launch(
            block = {
                val commentInfo=repo.commentsInfo(id)
                commentCount.value=commentInfo.comments
                mCommentInfo.value=commentInfo
                likeCount.value=commentInfo.popularity
            },
            onError = {
                toastMsg.value=it
            },
            onComplete = {
            }
        )


    }




}