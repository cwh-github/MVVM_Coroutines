package com.cwh.mvvm_coroutines_base.base.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cwh.mvvm_base.base.ext.click

/**
 * Description:
 * Date：2020/1/8 0008-11:40
 * Author: cwh
 */
abstract class BaseRecyclerViewAdapter<T>(val mContext: Context, data: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_VIEW = 0x10000111
        const val LOAD_MORE_VIEW = 0x10000222
        const val FOOTER_VIEW = 0x10000333
        const val EMPTY_VIEW = 0x10000555
    }

    var mData= mutableListOf<T>()
        internal set

    init {
        mData.clear()
        mData.addAll(data)
    }

    /**
     * 在显示 EmptyView时，是否还显示HeaderView
     */
    var mEmptyViewWithHeaderView = false

    /**
     * 在显示 EmptyView时，是否还显示FooterView
     */
    var mEmptyViewWithFooterView = false

    /**
     * 添加头View时，都是添加到LinearLayout中，
     * 根据RecyclerView的orientation 来设置头中View
     * 的orientation
     */
    private lateinit var mHeadViews: LinearLayout

    /**
     * 添加底部View时，都是添加到LinearLayout中，
     * 根据RecyclerView的orientation 来设置底部中View
     * 的orientation
     */
    private lateinit var mFooterViews: LinearLayout

    /**
     * 当无填充数据时，显示该EmptyView
     */
    private lateinit var mEmptyView: FrameLayout


    /**
     * 加载更多的View,在不需要加载更多时，不设置即可
     */
    private lateinit var mLoadMoreView: BaseLoadMoreView

    /**
     * 是否正在加载更多中的状态
     */
    private var mIsLoading = false

    /**
     * 加载更多监听
     */
    lateinit var onLoadMoreListener: () -> Unit

    /**
     * 是否显示加载更多,并开启加载更多功能
     */
    private var isEnableLoadMore = false

    /**
     * 是否在没有更多数据时，隐藏加载更多，并取消加载更多功能
     */
    private var isNoMoreDataGone = false

    /**
     * 添加Head View
     * @param view 需要添加的View
     * @param index 插入的在HeaderView中位置
     * @param orientation headerView 的方向，Vertical 宽度match_parent,
     *        horizontal,高度为match_parent
     *
     * @return 返回添加的View在HeaderView中的位置,未添加HeaderView 返回-1
     */
    fun addHeaderView(view: View, index: Int = -1, orientation: Int = LinearLayout.VERTICAL): Int {
        if (hasEmptyView() && !mEmptyViewWithHeaderView) {
            return -1
        }
        if (!::mHeadViews.isInitialized) {
            mHeadViews = LinearLayout(mContext)
            mHeadViews.orientation = orientation
            if (orientation == LinearLayout.VERTICAL) {
                mHeadViews.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            } else {
                mHeadViews.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
        val childCount = mHeadViews.childCount
        var mIndex = index
        if (index < 0 || index > childCount) {
            mIndex = childCount
        }
        mHeadViews.addView(view, mIndex)
        if (mHeadViews.childCount == 1) {
            mIndex = 0
            notifyItemInserted(0)
        }
        return mIndex
    }

    /**
     * 添加Footer View
     * @param view 需要添加的View
     * @param index 插入的在FooterView中位置
     * @param orientation FooterView 的方向，Vertical 宽度match_parent,
     *        horizontal,高度为match_parent
     *
     * @return 返回添加的View在FooterView中的位置,未添加FooterView 返回-1
     */
    fun addFooterView(view: View, index: Int = -1, orientation: Int = LinearLayout.VERTICAL): Int {
        if (hasEmptyView() && !mEmptyViewWithFooterView) {
            return -1
        }
        var mIndex = index
        if (!::mFooterViews.isInitialized) {
            mFooterViews = LinearLayout(mContext)
            mFooterViews.orientation = orientation
            if (orientation == LinearLayout.VERTICAL) {
                mFooterViews.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            } else {
                mFooterViews.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
        val childCount = mFooterViews.childCount
        if (index < 0 || index > childCount) {
            mIndex = childCount
        }
        mFooterViews.addView(view, mIndex)
        if (mFooterViews.childCount == 1) {
            mIndex = 0
            notifyItemInserted(getFooterViewPosition())
        }
        return mIndex
    }

    /**
     * 是否添加有在数据为空时的EmptyView(在没有数据时，才会将此View添加到
     * RecyclerView中)
     */
    fun hasEmptyView(): Boolean {
        return (mData.isEmpty() && ::mEmptyView.isInitialized
                && mEmptyView.childCount > 0)
    }

    /**
     * 是否拥有HeaderView
     */
    fun hasHeaderView(): Boolean {
        return (::mHeadViews.isInitialized && mHeadViews.childCount > 0)
    }

    /**
     * 是否拥有FooterView
     */
    fun hasFooterView(): Boolean {
        return (::mFooterViews.isInitialized && mFooterViews.childCount > 0)
    }

    /**
     * 是否拥有LoadMoreView
     */
    fun hasLoadMoreView(): Boolean {
        if (!::mLoadMoreView.isInitialized || !isEnableLoadMore) {
            return false
        }

        if (mLoadMoreView.mStatus == LoadMoreStatus.NO_MORE_DATA && isNoMoreDataGone) {
            return false
        }

        return true
    }

    /**
     * 获取FooterView在RecyclerView中的position
     */
    private fun getFooterViewPosition(): Int {
        var position: Int
        if (hasEmptyView()) {
            position = 1
            if (hasHeaderView() && mEmptyViewWithHeaderView) {
                position++
            }
        } else {
            position = if (hasHeaderView()) mData.size + 1 else mData.size
        }
        return position
    }

    /**
     * 设置EmptyView,当无填充数据时，显示EmptyView
     * @param isCustom 是否为定制的EmptyView的高度，默认不是，默认铺满全屏
     */
    fun setEmptyView(view: View,isCustom:Boolean=false) {
        //当没有初始化EmptyView时，需要初始化View,insert到列表中
        //当已经初始化完成，只需要将新View添加到EmptyView即可
        var insert = false
        if (!::mEmptyView.isInitialized) {
            mEmptyView = FrameLayout(mContext)
            if(isCustom){
                mEmptyView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }else{
                mEmptyView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            insert = true
        }
        mEmptyView.removeAllViews()
        mEmptyView.addView(view)
        if (insert && hasEmptyView()) {
            var position = 0
            if (hasHeaderView() && mEmptyViewWithHeaderView) {
                position++
            }
            notifyItemInserted(position)
        }
    }

    /**
     * 设置加载更多数据时的View(包括LoadMore  LoadSuccess LoadFail NoMoreData)
     */
    fun setLoadMoreView(loadMoreView: BaseLoadMoreView) {
        if (!::mLoadMoreView.isInitialized) {
            mLoadMoreView = loadMoreView
        }
        if (!hasLoadMoreView() && !hasEmptyView()) {
            isEnableLoadMore = true
            val position =
                mData.size + if (hasHeaderView()) 1 else 0 + if (hasFooterView()) 1 else 0
            notifyItemInserted(position)
        }
    }

    override fun getItemCount(): Int {
        var count = mData.size
        if (hasEmptyView()) {
            count = 1
            if (hasHeaderView() && mEmptyViewWithHeaderView) {
                count++
            }
            if (hasFooterView() && mEmptyViewWithFooterView) {
                count++
            }
            return count
        } else {
            if (hasHeaderView()) {
                count++
            }
            if (hasFooterView()) {
                count++
            }
            if (hasLoadMoreView()) {
                count++
            }
            return count
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (hasEmptyView()) {
            val showHeaderView = hasHeaderView() && mEmptyViewWithHeaderView
            return when (position) {
                0 -> {
                    if (showHeaderView) {
                        HEADER_VIEW
                    } else {
                        EMPTY_VIEW
                    }
                }
                1 -> {
                    if (showHeaderView) {
                        EMPTY_VIEW
                    } else {
                        FOOTER_VIEW
                    }
                }
                2 -> FOOTER_VIEW
                else -> EMPTY_VIEW
            }
        } else {
            val showHeaderView = hasHeaderView()
            //实际数据对应的position,对数据position,已进行调整，对应数据在mData中的位置
            var dataPosition = position
            if (showHeaderView) {
                dataPosition = position - 1
            }
            if (hasHeaderView() && position == 0) {
                return HEADER_VIEW
            } else {
                //不能用itemCount 计算ViewType，在Item notify后，itemCount 还未变
                return if (dataPosition < mData.size) {
                    getContentViewItemViewType(dataPosition)
                } else {
                    val leftSize = dataPosition - mData.size
                    val footerNum = if (hasFooterView()) 1 else 0
                    if (leftSize < footerNum) {
                        FOOTER_VIEW
                    } else {
                        LOAD_MORE_VIEW
                    }
                }
            }
        }
    }

//when (position) {
//    0 -> {
//        if (showHeaderView) {
//            BaseRecyclerViewAdapter.HEADER_VIEW
//        } else {
//            getContentViewItemViewType(dataPosition)
//        }
//    }
//
//    itemCount - 1 -> {
//        if (hasLoadMoreView()) {
//            BaseRecyclerViewAdapter.LOAD_MORE_VIEW
//        } else {
//            if (showFooterView) {
//                BaseRecyclerViewAdapter.FOOTER_VIEW
//            } else {
//                getContentViewItemViewType(dataPosition)
//            }
//        }
//    }
//
//    itemCount - 2 -> {
//        if (hasLoadMoreView()) {
//            if (showFooterView) {
//                BaseRecyclerViewAdapter.FOOTER_VIEW
//            } else {
//                getContentViewItemViewType(dataPosition)
//            }
//        } else {
//            getContentViewItemViewType(dataPosition)
//        }
//
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            HEADER_VIEW -> {
                val parent: ViewParent? = mHeadViews.parent
                if (parent is ViewGroup) {
                    parent.removeView(mHeadViews)
                }
                HeaderViewHolder(mHeadViews)
            }

            EMPTY_VIEW -> {
                val parent: ViewParent? = mEmptyView.parent
                if (parent is ViewGroup) {
                    parent.removeView(mEmptyView)
                }
                EmptyViewHolder(mEmptyView)
            }

            FOOTER_VIEW -> {
                val parent: ViewParent? = mFooterViews.parent
                if (parent is ViewGroup) {
                    parent.removeView(mFooterViews)
                }
                FooterViewHolder(mFooterViews)

            }

            LOAD_MORE_VIEW -> {
                val rootView = mLoadMoreView.getRootView()
                rootView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                LoadMoreViewHolder(rootView)
            }

            else -> {
                onCreateContentViewHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER_VIEW -> {
            }
            EMPTY_VIEW -> {
            }
            FOOTER_VIEW -> {
            }
            LOAD_MORE_VIEW -> {
                when (mLoadMoreView.mStatus) {
                    LoadMoreStatus.LOADING -> {
                        mLoadMoreView.showLoadingView()
                        if (!mIsLoading) {
                            mIsLoading = true
                            if (::onLoadMoreListener.isInitialized) {
                                onLoadMoreListener()
                            }
                        }
                    }

                    LoadMoreStatus.SUCCESS -> {
                        mLoadMoreView.showLoadSuccessView()
                        if (!mIsLoading) {
                            mIsLoading = true
                            mLoadMoreView.showLoadingView()
                            if (::onLoadMoreListener.isInitialized) {
                                onLoadMoreListener()
                            }
                        } else {
                            mLoadMoreView.showLoadingView()
                        }
                    }

                    LoadMoreStatus.FAIL -> {
                        mLoadMoreView.showLoadFailView()
                        val loadFailView = mLoadMoreView.getLoadFailView()
                        loadFailView.click {
                            if (!mIsLoading) {
                                mIsLoading = true
                                mLoadMoreView.showLoadingView()
                                if (::onLoadMoreListener.isInitialized) {
                                    onLoadMoreListener()
                                }
                            } else {
                                mLoadMoreView.showLoadingView()
                            }

                        }

                    }

                    LoadMoreStatus.NO_MORE_DATA -> {
                        mLoadMoreView.showNoMoreDataView()
                        mIsLoading = false
                    }
                }


            }
            else -> {
                var dataPosition=position
                if(hasHeaderView()){
                    dataPosition -= 1
                }
                onBindContentViewHolder(holder, dataPosition)
            }
        }
    }

    //对于GridLayoutManager ,Head Empty Foot Load More,设置为单独占一行或一列
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        layoutManager?.let {
            if (layoutManager is GridLayoutManager) {
                val defSpanSizeLookup = layoutManager.spanSizeLookup
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (getItemViewType(position)) {
                            HEADER_VIEW -> layoutManager.spanCount
                            EMPTY_VIEW -> layoutManager.spanCount
                            FOOTER_VIEW -> layoutManager.spanCount
                            LOAD_MORE_VIEW -> layoutManager.spanCount
                            else -> defSpanSizeLookup.getSpanSize(position)
                        }

                    }

                }
            }
        }
    }

    //对于StaggeredGridLayoutManager ，Head Empty Foot Load More，设置为单独占一行
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams?.let {
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                when (holder.itemViewType) {
                    HEADER_VIEW, EMPTY_VIEW, FOOTER_VIEW, LOAD_MORE_VIEW -> {
                        layoutParams.isFullSpan = true
                    }

                    else -> {
                        layoutParams.isFullSpan = false
                    }
                }
            }
        }
    }

    /**
     * 是否可以加载更多，主要用在当在下拉刷新状况下,
     * 不需要加载更多同时进行
     */
    fun enableLoadMore(enable: Boolean) {
        if (hasLoadMoreView()) {
            isEnableLoadMore = enable
            if (!enable) {
                val position = mData.size + if (hasHeaderView()) 1 else 0
                +if (hasFooterView()) 1 else 0
                //notifyItemRemoved(position)
                notifyDataSetChanged()
            } else {
                //do nothing
            }
        } else {
            isEnableLoadMore = enable
            if (enable) {
                if (::mLoadMoreView.isInitialized) {
                    mLoadMoreView.mStatus = LoadMoreStatus.LOADING
                    mIsLoading = false
                    val position = mData.size + if (hasHeaderView()) 1 else 0
                    +if (hasFooterView()) 1 else 0
                    notifyItemInserted(position + 1)
                }
            } else {
                //do nothing
            }
        }

    }

    /**
     * 加载更多数据成功
     *
     * 添加数据到list后,调用该方法
     */
    fun loadMoreSuccess() {
        mIsLoading = false
        if (!hasLoadMoreView()) {
            return
        }
        mLoadMoreView.mStatus = LoadMoreStatus.LOADING
        val position = mData.size + if (hasHeaderView()) 1 else 0
        //notifyItemChanged(position)
        notifyDataSetChanged()
    }

    /**
     * 没有更多数据
     *
     * 添加数据到list后,调用该方法
     *
     * @param gone 是否隐藏没有更多数据时的View
     */
    fun loadNoMoreData(gone: Boolean = false) {
        mIsLoading = false
        if (!hasLoadMoreView()) {
            return
        }
        isNoMoreDataGone = gone
        mLoadMoreView.mStatus = LoadMoreStatus.NO_MORE_DATA
        mLoadMoreView.showNoMoreDataView()
        val position = mData.size + if (hasHeaderView()) 1 else 0
        if (gone) {
            notifyItemRemoved(position)
        } else {
            //notifyItemChanged(position)
            notifyDataSetChanged()
        }

    }

    /**
     * 加载数据失败
     */
    fun loadFail() {
        mIsLoading = false
        if (!hasLoadMoreView()) {
            return
        }
        mLoadMoreView.mStatus = LoadMoreStatus.FAIL
        notifyDataSetChanged()
        //use notifyItemChanged cause Called attach on a child which is not detached
        //https://juejin.im/post/59faccf46fb9a0451704885b
        //notifyItemChanged(position)
    }

    /**
     * 移除HeaderView上指定的子View
     */
    fun removeHeaderView(view: View) {
        if (!hasHeaderView()) {
            return
        }
        mHeadViews.removeView(view)
        if (mHeadViews.childCount == 0) {
            notifyItemRemoved(0)
        }
    }

    /**
     * 移除FooterView上指定的子View
     */
    fun removeFooterView(view: View) {
        if (!hasFooterView()) {
            return
        }
        mFooterViews.removeView(view)
        if (mFooterViews.childCount == 0) {
            if (hasEmptyView()) {
                if (hasHeaderView() && mEmptyViewWithHeaderView) {
                    notifyItemRemoved(2)
                } else {
                    notifyItemRemoved(1)
                }
            } else {
                val position = if (hasHeaderView()) mData.size + 1 else mData.size
                notifyItemRemoved(position)
            }
        }
    }

    /**
     * 移除HeaderView指定位置的子View
     */
    fun removeHeaderView(index: Int) {
        if (!hasHeaderView()) {
            return
        }
        val count = mHeadViews.childCount
        if (index < 0 || index >= count) {
            throw IndexOutOfBoundsException("Index out of bounds")
        }
        mHeadViews.removeViewAt(index)
        if (mHeadViews.childCount == 0) {
            notifyItemRemoved(0)
        }
    }

    /**
     * 移除FooterView指定位置的子View
     */
    fun removeFooterView(index: Int) {
        if (!hasFooterView()) {
            return
        }
        val count = mFooterViews.childCount
        if (index < 0 || index >= count) {
            throw IndexOutOfBoundsException("Index out of bounds")
        }
        mFooterViews.removeViewAt(index)
        if (mFooterViews.childCount == 0) {
            if (hasEmptyView()) {
                if (hasHeaderView() && mEmptyViewWithHeaderView) {
                    notifyItemRemoved(2)
                } else {
                    notifyItemRemoved(1)
                }
            } else {
                val position = if (hasHeaderView()) mData.size + 1 else mData.size
                notifyItemRemoved(position)
            }
        }
    }

    /**
     * 移除HeaderView(全部移除)
     */
    fun removeAllHeaderView(){
        if (!hasHeaderView()) {
            return
        }
        mHeadViews.removeAllViews()
        if (mHeadViews.childCount == 0) {
            notifyDataSetChanged()
        }
    }

    /**
     * 移除FooterView(全部移除)
     */
    fun removeAllFooterView(){
        if (!hasFooterView()) {
            return
        }
        mFooterViews.removeAllViews()
        if (mFooterViews.childCount == 0) {
            notifyDataSetChanged()
        }

    }


    /**
     * 设置新数据给RecyclerView 用于刷新数据时使用
     *
     * @param isRefreshForEmptyData 当刷新后的数据为空时，是否显示到RecyclerView上
     */
    fun setNewData(data: MutableList<T>, isRefreshForEmptyData: Boolean = false) {
        enableLoadMore(true)
        mIsLoading = false
        if (::mLoadMoreView.isInitialized) {
            mLoadMoreView.mStatus = LoadMoreStatus.LOADING
        }
        if (isRefreshForEmptyData) {
            mData.clear()
            mData.addAll(data)
            notifyDataSetChanged()
        } else {
            if (data.isNotEmpty()) {
                mData.clear()
                mData.addAll(data)
                notifyDataSetChanged()
            }
        }
    }

    /**
     * 加载更多数据到RecyclerView
     */
    fun addData(data: MutableList<T>) {
        if (data.isNotEmpty()) {
            val oldSize = mData.size
            mData.addAll(data)
            val position = oldSize + (if (hasHeaderView()) 1 else 0)
            notifyItemRangeInserted(position, data.size)
        }
    }

    /**
     * 应用DiffUtil 得出的result应用到RecyclerView上
     * @param diffResult 通过DiffUtil 得出的Diff 结果
     * @param newData 新的数据源
     */
    fun applyDiffResult(diffResult: DiffUtil.DiffResult, newData: MutableList<T>) {
        diffResult.dispatchUpdatesTo(BaseRecyclerViewListUpdateCallback(this))
        this.mData = newData
    }

    /**
     * 重写此方法，设置需要显示的Content数据的ViewType,
     *
     * 如果没有多种ViewType 返回 0 即可
     *
     * @param position position已经过处理，对应的即是在mData中的位置
     */
    open fun getContentViewItemViewType(position: Int): Int=0

    /**
     * 重写此方法，实现Content数据创建ViewHolder
     * @param viewType getContentViewItemViewType返回的ViewType
     */
    abstract fun onCreateContentViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder

    /**
     * 重写此方法，实现Content数据与View的绑定
     * @param position 已经过调整的position,对应mData中的position
     */
    abstract fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int)


}

//HeaderView ViewHolder
class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//FooterView ViewHolder
class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//EmptyView ViewHolder
class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//LoadMoreView ViewHolder
class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)