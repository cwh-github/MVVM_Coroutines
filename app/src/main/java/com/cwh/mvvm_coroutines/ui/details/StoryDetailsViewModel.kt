package com.cwh.mvvm_coroutines.ui.details

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cwh.mvvm_coroutines.model.BeforeNews
import com.cwh.mvvm_coroutines.model.CommentInfo
import com.cwh.mvvm_coroutines.model.NewsDetails
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.repository.BeforeNewsRepository
import com.cwh.mvvm_coroutines.model.repository.NewsDetailsRepository
import com.cwh.mvvm_coroutines_base.base.Event
import com.cwh.mvvm_coroutines_base.base.ExceptionHandle
import com.cwh.mvvm_coroutines_base.base.Result
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
     * 收藏或取消收藏
     */
    val isLike=MutableLiveData<Event<Boolean>>()

    val mStory=MutableLiveData<Event<Story>>()

    val mDetails=MutableLiveData<Result<NewsDetails>>()

    //之前的news
    val beforeNews=MutableLiveData<Result<BeforeNews>>()

    val otherRepo=BeforeNewsRepository()


    /**
     * 获取新闻详情
     */
    fun newsDetails(id:Long){
        launch(
            onStart = {
                mDetails.value=Result.loading(null)
            },
            block = {
                val details=repo.newsDetails(id)
                mDetails.value= Result.success(details)
            },
            onError = {
                mDetails.value= Result.error(null)
            }

        )
    }

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

    /**
     * 收藏story
     */
    fun likeOrUnLikeStory(id:Long){
        launch(
            block = {
                val result=repo.likeOrUnLikeStory(id)
                isLike.value=Event(result)
            },
            onError = {
            }
        )
    }

    fun storyById(id:Long){
        launch(
            block = {
                val result=repo.storyById(id)
                result?.let {
                    mStory.value=Event(it)
                }
            },
            onError = {

            }
        )
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
            val repoStory=repo.storyById(story.id)
            story.isLike=repoStory?.isLike?:false
            story.isRead=true
            repo.readStory(story)
        }
    }




}