package com.cwh.mvvm_coroutines.ui.like

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwh.mvvm_coroutines_base.base.ext.click
import com.cwh.mvvm_coroutines_base.base.ext.find
import com.cwh.mvvm_coroutines.BR
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.databinding.LikeBinding
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.ui.details.StoryDetailsActivity
import com.cwh.mvvm_coroutines.utils.GlideUtils
import com.cwh.mvvm_coroutines_base.base.Status
import com.cwh.mvvm_coroutines_base.base.adapter.BaseRecyclerViewAdapter
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_like_story.*
import kotlinx.android.synthetic.main.toolbar.view.*

class LikeStoryActivity : BaseActivity<LikeStoryViewModel, LikeBinding>() {
    override val mViewModel: LikeStoryViewModel
            by lazy { createViewModel(this, LikeStoryViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_like_story
    override val mViewModelVariableId: Int
        get() = BR.likeViewModel

    private var mAdapter:NewsListAdapter?=null

    override fun initDataAndView() {
        mToolBar.mImgBack.click { finish() }
        mToolBar.mTvTitle.text = "我的收藏"
    }

    override fun onResume() {
        super.onResume()
        mRefresh.isRefreshing = true
        mViewModel.likeStoryList()
    }

    override fun initViewObserver() {
        mViewModel.mLikeStories.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> mRefresh.isRefreshing = true
                Status.ERROR -> mRefresh.isRefreshing = false
                Status.SUCCESS -> {
                    mRefresh.isRefreshing = false
                    initRecyclerView(it.data)
                }

            }
        })
    }

    private fun initRecyclerView(data: List<Story>?) {
        val mData = mutableListOf<Story>()
        data?.let {
            mData.addAll(data)
        }
        if (mAdapter==null) {
            mRecyclerView.layoutManager=LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false)
            mAdapter=NewsListAdapter(this,mData)
            mRecyclerView.adapter=mAdapter
        } else {
            mAdapter!!.setNewData(mData,true)
        }
    }

    inner class NewsListAdapter(mContext: Context, mData: MutableList<Story>) :
        BaseRecyclerViewAdapter<Story>(mContext, mData) {

        override fun onCreateContentViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {

            return NewsContentViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_news_content,
                    parent,
                    false
                )
            )

        }

        override fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val story = mData[position]
            val mViewHolder = holder as NewsContentViewHolder
            mViewHolder.mTvTitle.text = story.title
            mViewHolder.mTvAuthor.text = story.hint
            GlideUtils.loadRoundImage(
                mContext, story.images?.get(0) ?: "",
                mImg = mViewHolder.mImg
            )
            holder.itemView.click {
                startActivity(
                    Intent(mContext, StoryDetailsActivity::class.java)
                        .putExtra("story", story)
                )
            }
        }

    }

    inner class NewsContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImg = itemView.find<ImageView>(R.id.mImg)
        val mTvTitle = itemView.find<TextView>(R.id.mTvTitle)
        val mTvAuthor = itemView.find<TextView>(R.id.mTvAuthor)
    }

}
