package com.cwh.mvvm_coroutines_base.base.adapter

import androidx.recyclerview.widget.ListUpdateCallback

/**
 * Description:对于使用BaseRecyclerViewAdapter使用DiffUtil时，需要
 * 用此类创建ListUpdateCallback来更新列表，不要用result.dispatchUpdatesTo(mAdapter)
 * 因为在此时的adapter中，不止包含了data数据，还包含了header footer 和 loadmore view等
 * 所以需要重写ListUpdateCallback来自己处理更新
 *
 *  建议写为：result.dispatchUpdatesTo(BaseRecyclerViewListUpdateCallback(mAdapter))
 *  此处adapter经过处理了header footer loadmore 的view item
 *
 *  DiffUtil的作用是对两个列表进行比较，得出差异的结果，对于如何应用和是否应用到adapter上，
 *  由自己定义的ListUpdateCallback决定，对于result.dispatchUpdatesTo(mAdapter)可以这样调用，
 *  是因为其在内部创建了一个以Adapter作为成员变量的ListUpdateCallback
 * Date：2020/5/20 0020-13:59
 * Author: cwh
 */
class BaseRecyclerViewListUpdateCallback(private val adapter: BaseRecyclerViewAdapter<*>) :
    ListUpdateCallback {


    override fun onChanged(position: Int, count: Int, payload: Any?) {
        val headCount = if (adapter.hasHeaderView()) 1 else 0
        adapter.notifyItemRangeChanged(position + headCount, count, payload)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        val headCount = if (adapter.hasHeaderView()) 1 else 0
        adapter.notifyItemMoved(fromPosition + headCount, toPosition + headCount)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position + if (adapter.hasHeaderView()) 1 else 0, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        val headCount = if (adapter.hasHeaderView()) 1 else 0
        if (adapter.hasLoadMoreView() && adapter.itemCount == 0) {
            adapter.notifyItemRangeRemoved(position + headCount, count + 1)
        } else {
            adapter.notifyItemRangeRemoved(position + headCount, count)
        }
    }
}