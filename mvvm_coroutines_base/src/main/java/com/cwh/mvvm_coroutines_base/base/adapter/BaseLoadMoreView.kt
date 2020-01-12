package com.cwh.mvvm_coroutines_base.base.adapter

import android.content.Context
import android.view.View
import androidx.core.view.isVisible

/**
 * Description:加载更多View实例，如需要自己实现，需要实现BaseLoadMoreView
 * Date：2020/1/8 0008-16:37
 * Author: cwh
 */
abstract class BaseLoadMoreView constructor(val context: Context) {

    public var mStatus: LoadMoreStatus = LoadMoreStatus.LOADING
    /**
     * 获取加载更多数据的View
     */
    abstract fun getRootView(): View

    /**
     * 获取正在加载中的显示的View
     */
    abstract fun getLoadingView(): View

    /**
     * 获取加载成功的View
     */
    abstract fun getLoadSuccessView(): View

    /**
     * 获取加载失败的View
     */
    abstract fun getLoadFailView(): View

    /**
     * 获取没有更多数据可以加载时的View
     */
    abstract fun getNoMoreDataView(): View

    /**
     * 展示正在加载数据View
     */
    fun showLoadingView() {
        mStatus = LoadMoreStatus.LOADING
        getRootView().isVisible = true
        getLoadingView().isVisible = true
        getLoadSuccessView().isVisible = false
        getLoadFailView().isVisible = false
        getNoMoreDataView().isVisible = false
    }

    /**
     * 展示加载成功数据的View
     */
    fun showLoadSuccessView() {
        mStatus = LoadMoreStatus.SUCCESS
        getRootView().isVisible = true
        getLoadingView().isVisible = false
        getLoadSuccessView().isVisible = true
        getLoadFailView().isVisible = false
        getNoMoreDataView().isVisible = false
    }

    /**
     * 展示加载失败数据的View
     */
    fun showLoadFailView() {
        mStatus = LoadMoreStatus.FAIL
        getRootView().isVisible = true
        getLoadingView().isVisible = false
        getLoadSuccessView().isVisible = false
        getLoadFailView().isVisible = true
        getNoMoreDataView().isVisible = false

    }

    /**
     * 展示没有更多数据的View
     */
    fun showNoMoreDataView() {
        mStatus = LoadMoreStatus.NO_MORE_DATA
        getRootView().isVisible = true
        getLoadingView().isVisible = false
        getLoadSuccessView().isVisible = false
        getLoadFailView().isVisible = false
        getNoMoreDataView().isVisible = true
    }

}

enum class LoadMoreStatus {
    /**
     * 加载数据
     */
    LOADING,

    /**
     * 加载数据成功
     */
    SUCCESS,

    /**
     * 加载数据失败
     */
    FAIL,

    /**
     * 加载数据完成，且无更多数据
     */
    NO_MORE_DATA


}