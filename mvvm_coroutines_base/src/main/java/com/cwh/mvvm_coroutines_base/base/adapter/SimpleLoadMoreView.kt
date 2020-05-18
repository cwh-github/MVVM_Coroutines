package com.cwh.mvvm_coroutines_base.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.cwh.mvvm_base.base.ext.find
import com.cwh.mvvm_coroutines_base.R

/**
 * Description:加载更多View实例，如需要自己实现，需要实现BaseLoadMoreView
 * Date：2020/1/8 0008-17:02
 * Author: cwh
 */
class SimpleLoadMoreView(context: Context) : BaseLoadMoreView(context) {
    private var mRootView: View? = null

    init {
        mRootView = LayoutInflater.from(context).inflate(R.layout.layout_load_more, null)
    }

    override fun getRootView(): View {
        return mRootView!!
    }

    override fun getLoadingView(): View {
        return mRootView!!.find<View>(R.id.mLoading)
    }

    override fun getLoadSuccessView(): View = mRootView!!.findViewById(R.id.mLoadingSuccess)

    override fun getLoadFailView(): View = mRootView!!.findViewById(R.id.mLoadingFail)

    override fun getNoMoreDataView(): View = mRootView!!.findViewById(R.id.mNoMoreData)

}