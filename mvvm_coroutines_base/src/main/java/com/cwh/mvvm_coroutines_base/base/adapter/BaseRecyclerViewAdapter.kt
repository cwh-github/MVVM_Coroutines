package com.cwh.mvvm_coroutines_base.base.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * Description:
 * Date：2020/1/8 0008-11:40
 * Author: cwh
 */
abstract class BaseRecyclerViewAdapter<T>(val mContext: Context, val mData: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_VIEW = 0x10000111
        const val LOAD_MORE_VIEW = 0x10000222
        const val FOOTER_VIEW = 0x10000333
        const val EMPTY_VIEW = 0x10000555
    }

    /**
     * 在显示 EmptyView时，是否还显示HeaderView
     */
    private var mEmptyViewWithHeaderView = false

    /**
     * 在显示 EmptyView时，是否还显示FooterView
     */
    private var mEmptyViewWithFooterView = false

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
     * 添加Head View
     * @param view 需要添加的View
     * @param index 插入的在HeaderView中位置
     * @param orientation headerView 的方向，Vertical 宽度match_parent,
     *        horizontal,高度为match_parent
     *
     * @return 返回添加的View在HeaderView中的位置
     */
    fun addHeaderView(view: View, index: Int = -1, orientation: Int = LinearLayout.VERTICAL): Int {
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
     * @return 返回添加的View在FooterView中的位置
     */
    fun addFooterView(view: View, index: Int = -1, orientation: Int = LinearLayout.VERTICAL): Int {
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
        return (::mLoadMoreView.isInitialized)
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
     */
    fun setEmptyView(view: View) {
        //当没有初始化EmptyView时，需要初始化View,insert到列表中
        //当已经初始化完成，只需要将新View添加到EmptyView即可
        var insert = false
        if (!::mEmptyView.isInitialized) {
            mEmptyView = FrameLayout(mContext)
            mEmptyView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
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
            notifyItemInserted(itemCount)
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
            val showFooterView = hasFooterView()
            val dataSize = mData.size
            //实际数据对应的position,对数据position,已进行调整，对应数据在mData中的位置
            var dataPosition = position
            if (showHeaderView) {
                dataPosition = position - 1
            }
            return when (position) {
                0 -> {
                    if (showHeaderView) {
                        HEADER_VIEW
                    } else {
                        getContentViewItemViewType(dataPosition)
                    }
                }

                itemCount - 1 -> {
                    if (hasLoadMoreView()) {
                        LOAD_MORE_VIEW
                    } else {
                        if (showFooterView) {
                            FOOTER_VIEW
                        } else {
                            getContentViewItemViewType(dataPosition)
                        }
                    }
                }

                itemCount - 2 -> {
                    if (showFooterView) {
                        FOOTER_VIEW
                    } else {
                        getContentViewItemViewType(dataPosition)
                    }

                }

                else -> getContentViewItemViewType(dataPosition)
            }
        }
    }

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
                val mViewHolder = holder as
                        LoadMoreViewHolder
                when (mLoadMoreView.mStatus){
                    LoadMoreStatus.LOADING->{
                        mLoadMoreView.showLoadingView()
                    }

                    LoadMoreStatus.SUCCESS->{
                        mLoadMoreView.showLoadSuccessView()
                    }

                    LoadMoreStatus.FAIL->{
                        mLoadMoreView.showLoadFailView()
                    }

                    LoadMoreStatus.NO_MORE_DATA->{
                        mLoadMoreView.showNoMoreDataView()
                    }
                }


            }
            else -> {
                onBindContentViewHolder(holder, position)
            }
        }
    }

    /**
     * 重写此方法，设置需要显示的Content数据的ViewType,
     * @param position position已经过处理，对应的即是在mData中的位置
     */
    abstract fun getContentViewItemViewType(position: Int): Int

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