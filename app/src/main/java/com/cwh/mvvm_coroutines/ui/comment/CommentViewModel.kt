package com.cwh.mvvm_coroutines.ui.comment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cwh.mvvm_coroutines.model.Comment
import com.cwh.mvvm_coroutines.model.repository.CommentRepository
import com.cwh.mvvm_coroutines_base.base.Result
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingAction
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingCommand
import com.cwh.mvvm_coroutines_base.base.viewmodel.BaseViewModel

/**
 * Description:
 * Date：2020/3/6-17:31
 * Author: cwh
 */

class CommentViewModel(application: Application) : BaseViewModel<CommentRepository>(application) {
    override val repo: CommentRepository
        get() = CommentRepository()

    val mCommentList=MutableLiveData<Result<MutableList<Comment>>>()

    var mId:Long=0

    /**
     * 获取评论包括长评与短评
     */
    fun commentList(id:Long){
        mId=id
        launch(
            onStart = {
                mCommentList.value= Result.loading(null)
            },
            block = {
                val result=repo.comments(id)
                mCommentList.value= Result.success(result)
            },onError = {
               mCommentList.value= Result.error()
            }
        )


    }

    val command=BindingCommand(object :BindingAction{
        override fun call() {
            commentList(mId)
        }

    })

}