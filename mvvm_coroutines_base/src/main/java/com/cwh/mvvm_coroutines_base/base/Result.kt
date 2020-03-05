package com.cwh.mvvm_coroutines_base.base

/**
 * Description:带加载状态的结果
 *
 * 在正在加载时 可以返回loading 状态给livedata,通过观察livedata的status,
 * 显示不同UI
 *
 * Date：2020/3/4-18:51
 * Author: cwh
 */

data class Result<out T>(val status: Status, val data: T?, val message: String?,val code:Int?) {
    companion object {
        fun <T> success(data: T?): Result<T> {
            return Result(Status.SUCCESS, data, null,null)
        }

        fun <T> error(msg: String?,data:T?): Result<T> {
            return Result(Status.ERROR, data, msg,null)
        }

        fun <T> loading(data: T?): Result<T> {
            return Result(Status.LOADING, data, null,null)
        }

        fun <T> fail(msg:String,code:Int):Result<T>{
            return Result(Status.FAIL,null,msg,code)
        }

        fun <T> complete():Result<T>{
            return Result(Status.COMPLETE,null,null,null)
        }
    }
}

enum class Status{

    LOADING,

    SUCCESS,

    FAIL,

    ERROR,

    COMPLETE
}