package com.cwh.mvvm_coroutines_base.base.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cwh.mvvm_coroutines_base.Utils.LogUtils
import com.cwh.mvvm_coroutines_base.base.Event
import com.cwh.mvvm_coroutines_base.base.repository.IRepository
import kotlinx.coroutines.*

/**
 * Description:BaseViewModel 主要的数据逻辑操作在ViewModel中执行
 * 通过LiveData 或者 DataBinding驱动数据绑定到UI
 *
 *
 * Date：2019/12/31 0031-15:36
 * Author: cwh
 */
abstract class BaseViewModel<T : IRepository>(application: Application) :
    AndroidViewModel(application)
    , IBaseViewModel {

    abstract var repo: T

    val mDefUIEvent = DefUIEvent()

    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    /**
     * 指定在IO线程中调用
     */
    suspend fun launchOnIO(block: suspend CoroutineScope.() -> Unit) {
        withContext(Dispatchers.IO) {
            block()
        }
    }

    /**
     * 异常处理
     * @param block 正常处理逻辑
     * @param error 出现错误时的处理逻辑
     * @param finally 不管
     *
     */
    private suspend fun handleException(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(Throwable) -> Unit,
        finally: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                block()
            } catch (e: Exception) {
                LogUtils.e(
                    "ViewModel Error e.toString = " + e.toString() + "\n" + "exception:"
                            + e.message + "\n" + "cause: " + e.cause
                )
                if (e !is CancellationException) {
                    error(e)
                }
            } finally {
                finally()
            }
        }

    }

    /**
     * catch 了所有的在block中的Exception,不会出现抛出异常，
     * 所有Exception会在OnError中处理
     * @param block 需要处理的逻辑
     * @param onError 出现Exception时的逻辑
     * @param onComplete 不管处理成功或出现异常后的处理逻辑
     * @param isLaunchOnIO 是否在IO线程运行Block(默认在主线程运行)
     *
     */
    fun launch(
        block: suspend CoroutineScope.() -> Unit,
        onError: suspend CoroutineScope.(Throwable) -> Unit,
        onComplete: suspend CoroutineScope.() -> Unit,
        isLaunchOnIO: Boolean = false
    ) {
        launchOnUI {
            handleException({
                if (isLaunchOnIO) {
                    launchOnIO { block }
                } else {
                    block
                }
            }, {
                onError(it)
            }, {
                onComplete
            })

        }
    }


    /**
     * 一些常用的UI事件，通过LiveData来触发的封装
     *如：Toast 跳转Activity等
     *
     */
    inner class DefUIEvent {
        /**
         * Toast Event,设置value时，显示Toast
         */

        val mToastEvent = MutableLiveData<Event<String>>()

        /**
         * 是否显示正在加载的View  true:显示，false: 隐藏
         */

        val isShowLoadView = MutableLiveData<Event<Boolean>>()

        /**
         * 数据的加载状态，根据不同状态显示不同UI
         */
        val mLoadingState = MutableLiveData<Event<ViewState>>()

        /**
         * 触发返回事件
         */
        val onBackEvent = MutableLiveData<Event<Unit>>()

        /**
         * finish当前Activity
         */
        val onFinishEvent = MutableLiveData<Event<Unit>>()

        /**
         * 开启Activity(最常规的跳转Activity,不能携带参数和指定开始Activity mode)
         */
        val mStartActivity = MutableLiveData<Event<Class<*>>>()
    }

    /**
     * 数据的加载状态
     */
    enum class ViewState {

        LOADING,

        SUCCESS,

        FAILURE
    }

    override fun onAny() {
    }

    override fun onCreate() {
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPauese() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }
}