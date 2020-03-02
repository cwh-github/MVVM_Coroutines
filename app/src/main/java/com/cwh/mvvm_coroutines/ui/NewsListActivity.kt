package com.cwh.mvvm_coroutines.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.rangeTo
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.model.TopStory
import com.cwh.mvvm_coroutines.widget.NewsHeadView
import com.cwh.mvvm_coroutines_base.base.adapter.BaseRecyclerViewAdapter
import com.cwh.mvvm_coroutines_base.base.click
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import com.cwh.mvvm_coroutines_base.base.viewmodel.NoViewModel
import com.cwh.mvvm_coroutines_base.utils.TimeCovertUtils
import com.cwh.mvvm_coroutines_base.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_news_list.*
import kotlinx.android.synthetic.main.fragment_layout.*
import kotlinx.android.synthetic.main.fragment_layout.mRecyclerView
import kotlinx.android.synthetic.main.news_list_toolbar.view.*

class NewsListActivity : BaseActivity<NoViewModel,ViewDataBinding>() {
    override val mViewModel: NoViewModel
        by lazy { createViewModel(this,NoViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_news_list
    override val mViewModelVariableId: Int
        get() = -1

    private lateinit var mHeadView:NewsHeadView

    override fun initDataAndView() {
        initToolBar()
        initHeadView()
        initRecyclerView()
    }

    private fun initToolBar() {
        val curTime=TimeCovertUtils.covertToHourAndMin(System.currentTimeMillis())
        val hour=curTime.split(":")[0].toInt()
        mToolBar.mTvTitle.text=when{
            (hour in 6..10) ->{
                "早上好~"
            }
            (hour in 12..14)->{
                "中午好~"
            }
            (hour in 15..18)->{
                "下午好~"
            }
            (hour in 19..22)->{
                "晚上好~"
            }

            (hour in 22..24) || (hour in 0..4)->{
                "早点休息~"
            }

            else -> {
                "知乎日报"
            }
        }

        mToolBar.click {
            mRecyclerView.smoothScrollToPosition(0)
        }

        mToolBar.mImageDownload.click {
            ToastUtils.showToast(this,"Download")
        }

    }

    private fun initHeadView() {

//        "image_hue": "0xb3947d",
//        "hint": "作者 / 我是一只小萌刀",
//        "url": "https://daily.zhihu.com/story/9720746",
//        "image": "https://pic3.zhimg.com/v2-a2e68715865ee4717110c684ff34072e.jpg",
//        "title": "历史上有哪些英雄人物在晚年对自己一生的评价？",
//        "ga_prefix": "022411",
//        "type": 0,
//        "id": 9720746
        val data= mutableListOf<TopStory>()
        data.add(TopStory("","思考的聚集地",123,"https://pic3.zhimg.com/v2" +
                "-a2e68715865ee4717110c684ff34072e.jpg","0xb3947d","啊啊是看到积极的思考时间的时间大家都几十块的数据的技术等级撒基督教sad",0,"jsajd"))
        data.add(TopStory("","阿啥快递就撒就",123,"https://pic1.zhimg.com/v2" +
                "-f6a1c61c36fdf2968d3342b985bf1ac0.jpg","0x726650","阿萨巨大的技术交底",0,"jsajd"))

        data.add(TopStory("","阿啥快递就撒就",123,"https://pic2.zhimg.com/v2" +
                "-162fbbfbf55aba80dc8a50c7f989d67d.jpg","0x051e27","阿萨巨大的技术交底",
            0,"jsajd"))

        data.add(TopStory("","阿啥快递就撒就",123,"https://pic3.zhimg.com/v2" +
                "-081405b5574b887bf4521fe00596d562.jpg","0x051e27","阿萨巨大的技术交底",
            0,"jsajd"))

        data.add(TopStory("","阿啥快递就撒就",123,"https://pic2.zhimg.com/v2" +
                "-36de0401ed53cbaee5249c72bc29f44d.jpg","0x051e27","阿萨巨大的技术交底",
            0,"jsajd"))

        mHeadView=NewsHeadView(this,data)
    }

    override fun onStart() {
        super.onStart()
        mHeadView.startLoop()
    }

    override fun onStop() {
        super.onStop()
        mHeadView.stopLoop()
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager=LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        val adapter=NewsListAdapter(this, mutableListOf())
        adapter.mEmptyViewWithHeaderView=true
        adapter.addHeaderView(mHeadView.mRootView)
        mRecyclerView.adapter=adapter
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


    inner class NewsListAdapter(mContext: Context, mData: MutableList<String>) :
        BaseRecyclerViewAdapter<String>(mContext, mData) {
        override fun onCreateContentViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return NewsContentViewHolder(LayoutInflater.from(parent.context).
                inflate(R.layout.item_news_content,parent,false))
        }

        override fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

    }

    inner class NewsContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class NewsTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
