package com.cwh.mvvm_coroutines.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwh.mvvm_coroutines.BR
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.databinding.HomeViewDataBinding
import com.cwh.mvvm_coroutines.down.DownloadStoriesService
import com.cwh.mvvm_coroutines.model.BeforeNews
import com.cwh.mvvm_coroutines.model.LatestNews
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.model.TopStory
import com.cwh.mvvm_coroutines.ui.details.StoryDetailsActivity
import com.cwh.mvvm_coroutines.ui.details.StoryDetailsActivityForFragment
import com.cwh.mvvm_coroutines.ui.like.LikeStoryActivity
import com.cwh.mvvm_coroutines.utils.GlideUtils
import com.cwh.mvvm_coroutines.utils.SPUtils
import com.cwh.mvvm_coroutines.utils.TimeParseUtils
import com.cwh.mvvm_coroutines.widget.NewsHeadView
import com.cwh.mvvm_coroutines_base.base.Status
import com.cwh.mvvm_coroutines_base.base.adapter.BaseRecyclerViewAdapter
import com.cwh.mvvm_coroutines_base.base.adapter.SimpleLoadMoreView
import com.cwh.mvvm_coroutines_base.base.click
import com.cwh.mvvm_coroutines_base.base.find
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import com.cwh.mvvm_coroutines_base.utils.TimeCovertUtils
import com.cwh.mvvm_coroutines_base.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_news_list.*
import kotlinx.android.synthetic.main.fragment_layout.mRecyclerView
import kotlinx.android.synthetic.main.news_list_toolbar.view.*

class NewsListActivity : BaseActivity<NewsListViewModel, HomeViewDataBinding>() {

    override val mViewModel: NewsListViewModel
            by lazy { createViewModel(this, NewsListViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_news_list
    override val mViewModelVariableId: Int
        get() = BR.newsListViewModel

    private lateinit var mHeadView: NewsHeadView

    private var mAdapter: NewsListAdapter? = null

    private val mData= mutableListOf<Story>()

    private val twoHour=2*60*60*1000

    private lateinit var mImageDownLoad:ImageView

    private lateinit var mTvProgress:TextView

    private var mReceiver: MyBroadCastReceiver?=null

    override fun initDataAndView() {
        initToolBar()
        registerReceiver()
        val topStory = mutableListOf<TopStory>()
        initHeadView(topStory)
        mRefresh.isRefreshing = true
        mViewModel.latestNewsList()
//        mRefresh.setOnRefreshListener {
//            mViewModel.latestNewsList()
//        }
    }

    override fun initViewObserver() {
        mViewModel.latestNews.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> mRefresh.isRefreshing = true
                Status.ERROR -> mRefresh.isRefreshing = false
                Status.SUCCESS -> {
                    initRecyclerView(it.data!!)
                    mRefresh.isRefreshing=false
                    SPUtils.saveRefreshTime()
                }
            }
        })

        mViewModel.beforeNews.observe(this, Observer {
            when(it.status){
                Status.LOADING->{}
                Status.SUCCESS->{
                    val beforeNews=it.data
                    beforeNews?.let {
                        mAdapter!!.addData(formatData(it))
                        mAdapter!!.loadMoreSuccess()
                    }?:mAdapter!!.loadNoMoreData()

                }

                Status.ERROR->{
                    mAdapter!!.loadFail()
                }

            }
        })

    }

    private fun formatData(beforeNews: BeforeNews):MutableList<Story>{
        val date=beforeNews.date
        val result= mutableListOf<Story>()
        val story=Story()
        story.isTime=true
        story.date=date
        result.add(story)
        beforeNews.stories?.let {
            result.addAll(it)
        }
        return result
    }

    private fun initToolBar() {
        mTvProgress=mToolBar.find(R.id.mTvProgress)
        mImageDownLoad=mToolBar.find(R.id.mImageDownload)
        mImageDownLoad.click {
            DownloadStoriesService.startService(this)
            mImageDownLoad.isVisible=false
            mTvProgress.isVisible=true
        }
        val month = when (TimeCovertUtils.covertMonth()) {
            1 -> "一月"
            2 -> "二月"
            3 -> "三月"
            4 -> "四月"
            5 -> "五月"
            6 -> "六月"
            7 -> "七月"
            8 -> "八月"
            9 -> "九月"
            10 -> "十月"
            11 -> "十一月"
            12 -> "十二月"
            else -> "一月"
        }
        mToolBar.mTvMonth.text = month
        mToolBar.mTvDay.text = "${TimeCovertUtils.covertDay()}"

        mToolBar.click {
            val layoutManager=mRecyclerView.layoutManager as LinearLayoutManager?
            val position=layoutManager?.findFirstVisibleItemPosition()?:0
            if(position>20){
                mRecyclerView.scrollToPosition(20)
            }
            mRecyclerView.smoothScrollToPosition(0)
        }

    }

    private fun titleGreetings() {
        val curTime = TimeCovertUtils.covertToHourAndMin(System.currentTimeMillis())
        val hour = curTime.split(":")[0].toInt()
        mToolBar.mTvTitle.text = when {
            (hour in 6..10) -> {
                "早上好~ ( ‘-ωก̀ )"
            }
            (hour in 12..13) -> {
                "中午好~ (*•̀ㅂ•́)و"
            }
            (hour in 15..16) -> {
                "下午好~  ♪ (*´∀`*)"
            }
            (hour in 19..22) -> {
                "晚上好~ (•̀ᴗ•́)و"
            }

            (hour in 22..24) || (hour in 0..4) -> {
                "早点休息~ (:3[」]="
            }

            else -> {
                "知乎日报"
            }
        }

        mToolBar.mTvTitle.click {
            startActivity(Intent(this,LikeStoryActivity::class.java))
        }
    }

    private fun initHeadView(data: MutableList<TopStory>) {
        mHeadView = NewsHeadView(this, data)
        mHeadView.onItemClick={
            val story=Story(it.ga_prefix,it.hint!!,it.id.toLong(),it.image_hue!!, arrayListOf(it.image),
                it.title,it.type,it.url,false,false,it.id.toLong(),false,0)
            startActivity(Intent(this,StoryDetailsActivity::class.java)
                .putExtra("story",story))
        }
    }

    override fun onStart() {
        super.onStart()
        mHeadView.startLoop()
    }

    override fun onResume() {
        super.onResume()
        titleGreetings()
        autoRefresh()
    }

    override fun onStop() {
        super.onStop()
        mHeadView.stopLoop()
    }

    private fun autoRefresh(){
        if(kotlin.math.abs(System.currentTimeMillis() - SPUtils.getLastTime()) >=twoHour){
            mRecyclerView.scrollToPosition(0)
            mRefresh.isRefreshing=true
            mViewModel.latestNewsList()
        }
    }

    private fun initRecyclerView(data: LatestNews) {
        data.top_stories?.let {
            mHeadView.notifyDataChange(it)
        }
        if (mAdapter == null) {
            mRecyclerView.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL, false
            )
            data.stories?.let {
                mData.addAll(it)
            }
            mAdapter = NewsListAdapter(this,mData)
            mAdapter!!.mEmptyViewWithHeaderView = true
            mAdapter!!.addHeaderView(mHeadView.mRootView)
            mAdapter!!.setLoadMoreView(SimpleLoadMoreView(this))
            //加载更多
            mAdapter!!.onLoadMoreListener={
                if(mAdapter!!.mData.isNotEmpty()){
                    val date=mAdapter!!.mData[mAdapter!!.mData.size-1].date
                    mViewModel.beforeNewsList(date)
                }
            }
            mRecyclerView.adapter = mAdapter
//            mRecyclerView.addOnChildAttachStateChangeListener(object :
//                RecyclerView.OnChildAttachStateChangeListener{
//                override fun onChildViewDetachedFromWindow(view: View) {
//                    val position=mRecyclerView.getChildLayoutPosition(view)
//                    if(position==0){
//                        mHeadView.stopLoop()
//                    }
//                }
//
//                override fun onChildViewAttachedToWindow(view: View) {
//                    val position=mRecyclerView.getChildLayoutPosition(view)
//                    if(position==0){
//                        mHeadView.startLoop()
//                    }
//                }
//
//            })
        } else {
            data.stories?.let {
                val data= mutableListOf<Story>()
                data.addAll(it)
                mAdapter!!.setNewDate(data)
            }
        }
    }



    override fun onBackPressed() {
        moveTaskToBack(true)
    }


    inner class NewsListAdapter(mContext: Context, mData: MutableList<Story>) :
        BaseRecyclerViewAdapter<Story>(mContext, mData) {

        val TIME_CONTENT_TYPE = 1000
        val ITEM_CONTENT_TYPE = 10001

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
            super.onViewAttachedToWindow(holder)
            if(holder.itemViewType== HEADER_VIEW){
                mHeadView.startLoop()
            }
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            super.onViewDetachedFromWindow(holder)
            if(holder.itemViewType== HEADER_VIEW){
                mHeadView.stopLoop()
            }
        }

        override fun getContentViewItemViewType(position: Int): Int {
            val story=mData[position]
            return if(story.isTime){
                TIME_CONTENT_TYPE
            }else{
                ITEM_CONTENT_TYPE
            }
        }

        override fun onCreateContentViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {

            return when (viewType) {
                ITEM_CONTENT_TYPE ->
                    NewsContentViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_news_content,
                            parent,
                            false
                        )
                    )
                TIME_CONTENT_TYPE ->
                    NewsTimeViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_news_time,
                            parent,
                            false
                        )
                    )
                else ->
                    NewsContentViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_news_content,
                            parent,
                            false
                        )
                    )
            }
        }

        override fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val story=mData[position]
            when{
                holder.itemViewType==TIME_CONTENT_TYPE->{

                    val mViewHolder=holder as NewsTimeViewHolder
                    mViewHolder.mTvTime.text=TimeParseUtils.mothAndDay(story.date)

                }

                holder.itemViewType==ITEM_CONTENT_TYPE->{
                    val mViewHolder=holder as NewsContentViewHolder
                    if(story.isRead){
                        mViewHolder.mTvTitle.alpha=0.5f
                        mViewHolder.mTvAuthor.alpha=0.5f
                    }else{
                        mViewHolder.mTvTitle.alpha=1.0f
                        mViewHolder.mTvAuthor.alpha=1.0f
                    }
                    mViewHolder.mTvTitle.text=story.title
                    mViewHolder.mTvAuthor.text=story.hint
                    GlideUtils.loadRoundImage(mContext,story.images?.get(0)?:"",
                        mImg = mViewHolder.mImg)

                    holder.itemView.click {
//                        startActivity(Intent(mContext,StoryDetailsActivity::class.java)
//                            .putExtra("story",story))
                        val list=ArrayList<Story>()
                        var index=0
                        mData.forEach {
                            if(!it.isTime){
                                list.add(it)
                                if(it.id==story.id){
                                    index=list.size-1
                                }
                            }
                        }
                        startActivity(Intent(mContext,StoryDetailsActivityForFragment::class.java)
                            .putExtra("mStoryList",list)
                            .putExtra("index",index)
                        )
                        if(!story.isRead){
                            story.isRead=true
                            notifyDataSetChanged()
                            mViewModel.setStoryIsRead(story)
                        }

                    }

                }
            }
        }

    }

    inner class NewsContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mImg=itemView.find<ImageView>(R.id.mImg)
        val mTvTitle=itemView.find<TextView>(R.id.mTvTitle)
        val mTvAuthor=itemView.find<TextView>(R.id.mTvAuthor)
    }

    inner class NewsTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mTvTime=itemView.find<TextView>(R.id.mTvTime)
    }

    private fun registerReceiver() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val filter = IntentFilter()
        filter.addAction(DownloadStoriesService.DOWN_LOAD_START)
        filter.addAction(DownloadStoriesService.DOWN_LOAD_PROGRESS)
        filter.addAction(DownloadStoriesService.DOWN_LOAD_COMPLETE)
        mReceiver = MyBroadCastReceiver()
        localBroadcastManager.registerReceiver(mReceiver!!, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        mReceiver?.let {
            unregisterReceiver(it)
        }
    }

    inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                DownloadStoriesService.DOWN_LOAD_START -> {
                    mTvProgress.isVisible=true
                    mImageDownLoad.isVisible=false
                }
                DownloadStoriesService.DOWN_LOAD_PROGRESS -> {
                    val progress=intent!!.getIntExtra(DownloadStoriesService.PROGRESS,0)
                    mTvProgress.text="$progress %"
                }
                DownloadStoriesService.DOWN_LOAD_COMPLETE -> {
                    mImageDownLoad.isVisible=true
                    mTvProgress.text="0 %"
                    mTvProgress.isVisible=false
                    ToastUtils.showToast(this@NewsListActivity,"下载完成(•‾̑⌣‾̑•)✧˖°")
                }
            }
        }

    }
}
