package com.cwh.mvvm_coroutines.ui.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwh.mvvm_base.base.ext.click
import com.cwh.mvvm_base.base.ext.find
import com.cwh.mvvm_coroutines.BR
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.databinding.CommentBinding
import com.cwh.mvvm_coroutines.model.Comment
import com.cwh.mvvm_coroutines.utils.GlideUtils
import com.cwh.mvvm_coroutines.utils.TimeParseUtils
import com.cwh.mvvm_coroutines_base.base.Status
import com.cwh.mvvm_coroutines_base.base.adapter.BaseRecyclerViewAdapter
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.toolbar.view.*

class CommentActivity : BaseActivity<CommentViewModel,CommentBinding>() {
    override val mViewModel: CommentViewModel
        by lazy { createViewModel(this,CommentViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_comment
    override val mViewModelVariableId: Int
        get() = BR.commentViewModel
    private var id:Long=-1

    private var mCount=0

    private var mAdapter:CommentsAdapter?=null

    private var mLongCount=0
    private var mShortCount=0

    override fun initDataAndView() {
        id=intent.getLongExtra("id",-1L)
        mCount=intent.getIntExtra("count",0)
        mLongCount=intent.getIntExtra("mLongCount",0)
        mShortCount=intent.getIntExtra("mShortCount",0)
        mToolBar.mImgBack.click { finish() }
        mToolBar.mTvTitle.text=if(mCount>0){"${mCount}条评论"}else{"评论"}
        mViewModel.commentList(id)
        mRefresh.isRefreshing=true
    }

    override fun initViewObserver() {
        mViewModel.mCommentList.observe(this, Observer {
            LogUtils.d("LiveData","observe ")
            when(it.status){
                Status.LOADING->mRefresh.isRefreshing=true
                Status.ERROR->mRefresh.isRefreshing=false
                Status.SUCCESS->{
                    mRefresh.isRefreshing=false
                    initRecyclerView(it.data)
                }
            }

        })
    }

    private fun initRecyclerView(data: MutableList<Comment>?) {
        if(mAdapter==null){
            mRecyclerView.layoutManager=LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false)
            mAdapter=CommentsAdapter(this,formatData(data))
            mRecyclerView.adapter=mAdapter
        }else{
            mAdapter!!.setNewData(formatData(data))
        }
    }

    private fun formatData(data: MutableList<Comment>?):MutableList<Comment>{
        val result= mutableListOf<Comment>()
        val long= mutableListOf<Comment>()
        val short= mutableListOf<Comment>()
        if(!data.isNullOrEmpty()){
            data.forEach {
                if(it.isLong){
                    long.add(it)
                }else{
                    short.add(it)
                }
            }
        }
        if(long.isNotEmpty()){
            val first=Comment("","","",0,0,null,0,true,true)
            long.add(0,first)
            result.addAll(long)
        }
        if(short.isNotEmpty()){
            val first=Comment("","","",0,0,null,0,false,true)
            short.add(0,first)
            result.addAll(short)
        }
        LogUtils.d("Result","${result.joinToString()}")
        return result
    }


    inner class CommentsAdapter(mContext: Context, mData: MutableList<Comment>) :
        BaseRecyclerViewAdapter<Comment>(mContext, mData) {

        val TITLE_TYPE=100

        val ITEM_TYPE=1001

        override fun getContentViewItemViewType(position: Int): Int {
            val comment=mData[position]
            return if(comment.isTitle){
                TITLE_TYPE
            }else{
                ITEM_TYPE
            }
        }

        override fun onCreateContentViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return when(viewType){
                TITLE_TYPE->{
                    TitleViewHolder(LayoutInflater.from(parent.context).
                        inflate(R.layout.item_comment_title,parent,false))
                }

                ITEM_TYPE->{
                    ItemViewHolder(LayoutInflater.from(parent.context).
                        inflate(R.layout.item_comment_content,parent,false))
                }

                else->{
                    ItemViewHolder(LayoutInflater.from(parent.context).
                        inflate(R.layout.item_comment_content,parent,false))
                }
            }
        }

        override fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val comment=mData[position]
            when(holder.itemViewType){
                TITLE_TYPE->{
                    val mViewHolder=holder as TitleViewHolder
                    mViewHolder.mTvTitle.text=if(comment.isLong){
                        if(mLongCount>0){"${mLongCount}条长评"}else{"长评"}
                    }else{
                        if(mShortCount>0){"${mShortCount}条短评"}else{"短评"}
                    }
                }


                ITEM_TYPE->{
                    val mViewHolder=holder as ItemViewHolder
                    if(comment.reply_to==null){
                        mViewHolder.mTvReply.isVisible=false
                    }else{
                        val replyTo=comment.reply_to
                        mViewHolder.mTvReply.isVisible=true
                        mViewHolder.mTvReply.text="//${replyTo?.author}:${replyTo?.content}"
                    }
                    mViewHolder.mTvContent.text=comment.content
                    mViewHolder.mTvUserName.text=comment.author
                    mViewHolder.mTvLike.text=if(comment.likes==0){
                        ""
                    }else{
                        "${comment.likes}"
                    }
                    mViewHolder.mTvTime.text=TimeParseUtils.formatTime(comment.time)
                    GlideUtils.loadCircleImage(mContext,comment.avatar,mImg = mViewHolder.mImgUser)
                }

            }
        }

    }


    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mTvTitle=itemView.find<TextView>(R.id.mTvTitle)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mImgUser=itemView.find<ImageView>(R.id.mImgUser)
        val mTvUserName=itemView.find<TextView>(R.id.mTvUserName)
        val mTvContent=itemView.find<TextView>(R.id.mTvContent)
        val mTvReply=itemView.find<TextView>(R.id.mTvReply)
        val mTvTime=itemView.find<TextView>(R.id.mTvTime)
        val mTvLike=itemView.find<TextView>(R.id.mTvLike)
    }
}
