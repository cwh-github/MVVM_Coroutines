package com.cwh.mvvm_coroutines.Fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwh.mvvm_coroutines_base.base.ext.click
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines_base.base.adapter.BaseRecyclerViewAdapter
import com.cwh.mvvm_coroutines_base.base.adapter.SimpleLoadMoreView
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_layout.*
import kotlinx.android.synthetic.main.item_recycler_type_one.view.*

/**
 * Description:
 * Date：2020/1/9 0009-20:03
 * Author: cwh
 */
class GridFragment:Fragment() {

    private var mAdapter: MyGridAdapter? = null

    private val mData = mutableListOf<String>()

    /**
     * 是否显示No More Data text
     */
    private var isNoMoreDataGone = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initBtnEvent()
        initRefresh()
    }

    private fun initRefresh() {
        mRefresh.setOnRefreshListener {
            mAdapter?.enableLoadMore(false)
            mHandler.postDelayed({
                val data = mutableListOf<String>()
                for (i in 0..10) {
                    data.add("$i")
                }
                if (mAdapter == null) {
                    initRecyclerView()
                    mAdapter!!.setNewData(data)
                } else {
                    mAdapter!!.setNewData(data)
                }
                mRefresh.isRefreshing = false
                mAdapter?.enableLoadMore(true)
            }, 2000)
        }
    }

    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {

        }
    }

    private fun initRecyclerView() {
        if (mAdapter == null) {
            for (i in 0..30) {
                mData.add("$i")
            }
            mAdapter = MyGridAdapter(this.activity!!, mData)
            val emptyView = View.inflate(this.activity!!, R.layout.empty_view, null)
            mAdapter!!.setEmptyView(emptyView)
            mAdapter!!.setLoadMoreView(SimpleLoadMoreView(this.activity!!))
            mAdapter!!.onLoadMoreListener = {
                LogUtils.d("BaseRecyclerViewAdapter", "On Load More")
                mHandler.postDelayed({
                    //是否加载数据成功
                    val num = java.util.Random().nextInt(12)
                    val result = num % 2 == 0
                    LogUtils.d("BaseRecyclerViewAdapter", "num is $num,reslut is $result")
                    if (result) {
                        val data = mutableListOf<String>()
                        for (i in 0..10) {
                            data.add("$i")
                        }
                        mAdapter!!.addData(data)
                        LogUtils.d("BaseRecyclerViewAdapter", "Add Data Success")
                        //是否还有更多数据
                        val result = java.util.Random().nextInt(10) > 3
                        if (result) {
                            mAdapter!!.loadMoreSuccess()
                            LogUtils.d("BaseRecyclerViewAdapter", "Load More Data Success")
                        } else {
                            mAdapter!!.loadNoMoreData(isNoMoreDataGone)
                            LogUtils.d("BaseRecyclerViewAdapter", "No More Data")
                        }
                    } else {
                        mAdapter!!.loadFail()
                        LogUtils.d("BaseRecyclerViewAdapter", "Load Fail")
                    }

                }, 2000)
            }
            mRecyclerView.layoutManager = GridLayoutManager(this.context,3,GridLayoutManager.VERTICAL,false)
            mRecyclerView.adapter = mAdapter
            mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = dip2px(16f).toInt()
                    outRect.right=dip2px(16f).toInt()
                }
            })
        }

    }

    private fun initBtnEvent() {
        mBtnAddHead.click {
            val view = LayoutInflater.from(this.activity!!)
                .inflate(R.layout.item_recycler_type_one, mRecyclerView, false)
            view.mTvText.text = "Header View"
            mAdapter!!.addHeaderView(view)
        }

        mBtnRemoveHead.click {
            if (mAdapter!!.hasHeaderView()) {
                mAdapter!!.removeHeaderView(0)
            }
        }

        mBtnAddFooter.click {
            val view = LayoutInflater.from(this.activity!!)
                .inflate(R.layout.item_recycler_type_one, mRecyclerView, false)
            view.mTvText.text = "Footer View"
            mAdapter!!.addFooterView(view)
        }

        mBtnRemoveFooter.click {
            if (mAdapter!!.hasFooterView()) {
                mAdapter!!.removeFooterView(0)
            }
        }
        mBtnEnableLoadMore.click {
            mAdapter!!.enableLoadMore(true)
        }

        mBtnNoEnableLoadMore.click {
            mAdapter!!.enableLoadMore(false)
        }

        mBtnEmptyView.click {
            val datas = mutableListOf<String>()
            mAdapter!!.setNewData(datas, true)
            mAdapter!!.mEmptyViewWithFooterView = true
            mAdapter!!.mEmptyViewWithHeaderView = true
        }

        mBtnNoLoadMore.click {
            isNoMoreDataGone = true
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dip2px(dpValue: Float): Float {
        val scale = context!!.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

}


class MyGridAdapter(mContext: Context, mData: MutableList<String>) :
    BaseRecyclerViewAdapter<String>(mContext, mData) {
    val TYPE_ONE = 0
    val TYPE_TWO = 2

    override fun getContentViewItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            TYPE_ONE
        } else {
            TYPE_TWO
        }
    }

    override fun onCreateContentViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ONE) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_type_one, parent, false)
            GridViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_type_two, parent, false)
            GridViewHolder1(view)
        }


    }

    override fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = holder.itemViewType
        val mViewHolder = if (type == TYPE_ONE) holder as GridViewHolder
        else holder as GridViewHolder1
        mViewHolder.itemView.mTvText.text = "Position is $position"
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class GridViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView)
}