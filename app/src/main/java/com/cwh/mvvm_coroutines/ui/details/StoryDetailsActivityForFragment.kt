package com.cwh.mvvm_coroutines.ui.details

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cwh.mvvm_base.base.ext.click
import com.cwh.mvvm_coroutines.BR
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.databinding.StoryDetailsActivityFragmentBinding
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.ui.comment.CommentActivity
import com.cwh.mvvm_coroutines_base.base.Status
import com.cwh.mvvm_coroutines_base.base.observerEvent
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import com.cwh.mvvm_coroutines_base.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_story_details_for_fragment.*

class StoryDetailsActivityForFragment : BaseActivity<StoryDetailsViewModel,
        StoryDetailsActivityFragmentBinding>() {
    override val mViewModel: StoryDetailsViewModel
        by lazy { createViewModel(this,StoryDetailsViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_story_details_for_fragment
    override val mViewModelVariableId: Int
        get() = BR.fragmentActivityStoryDetails


    private var mStory: Story?=null
    private var mStoryList:ArrayList<Story>? = null
    private var mStoryId:Long?=null
    private var mCount=-1
    private var mLongCount=0
    private var mShortCount=0

    private var mViewPagerAdapter:StoryViewPagerAdapter?=null

    companion object{
        public val READ_LIST_CODE=1000
    }


    val mReadList= arrayListOf<Long>()

    override fun initDataAndView() {
        //设置状态栏透明
        val decorView = window.decorView
        var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        mStoryList=intent.getSerializableExtra("mStoryList") as? ArrayList<Story>
        val index=intent.getIntExtra("index",0)
        mStory=mStoryList?.get(index)
        initClickListener()
        mStory?.let {
            mStoryId=it.id
            mViewModel.commentInfo(it.id)
            mViewModel.storyById(mStoryId!!)
        }
        mStoryList?.let {
            initViewPager(index)
        }
    }



    override fun initViewObserver() {
        mViewModel.toastMsg.observe(this, Observer {
            ToastUtils.showToast(this,it)
        })

        mViewModel.commentCount.observe(this, Observer {
            mCount=it
        })

        mViewModel.mCommentInfo.observe(this, Observer {
            mLongCount=it.long_comments
            mShortCount=it.short_comments
        })

        mViewModel.mStory.observerEvent(this){
            if(it.isLike){
                mImgCollect.setImageResource(R.drawable.ic_collect_sel)
            }else{
                mImgCollect.setImageResource(R.drawable.ic_collect)
            }
        }

        mViewModel.isLike.observerEvent(this){
            if(it){
                mImgCollect.setImageResource(R.drawable.ic_collect_sel)
            }else{
                mImgCollect.setImageResource(R.drawable.ic_collect)
            }
        }

        mViewModel.beforeNews.observe(this, Observer {
            when(it.status){
                Status.LOADING->{}
                Status.SUCCESS->{
                    val beforeNews=it.data
                    beforeNews?.stories?.let {
                        mStoryList!!.addAll(it)
                        mViewPagerAdapter!!.notifyDataSetChanged()
                    }

                }

                Status.ERROR->{

                }

            }
        })

    }

    private fun initClickListener() {
        mImgBack.click {
            val intent=Intent()
            intent.putExtra("readList",mReadList)
            setResult(READ_LIST_CODE,intent)
            finish()
        }

        mRelMsg.click {
            LogUtils.d("Msg","$mCount  $mStoryId")
            if(mCount!=-1){
                mStoryId?.let {
                    startActivity(
                        Intent(this, CommentActivity::class.java)
                        .putExtra("id",it)
                        .putExtra("count",mCount)
                        .putExtra("mLongCount",mLongCount)
                        .putExtra("mShortCount",mShortCount)
                    )
                }
            }


        }

        mRelLike.click {
            ToastUtils.showToast(this,"并没有卵用 (ノ—_—)ノ~┴————┴ ")
        }

        mRelCollect.click {
            mStoryId?.let {
                mViewModel.likeOrUnLikeStory(mStoryId!!)
            }
        }

        mRelShare.click{
            ToastUtils.showToast(this,"并没有卵用 (ノ—_—)ノ~┴————┴ ")
        }
    }


    private fun initViewPager(index: Int) {
        mViewPagerAdapter=StoryViewPagerAdapter(this)
        //由于ViewPager2为RecyclerView,默认会预加载前后各两个item
        mViewPager.offscreenPageLimit=1
        mViewPager.adapter=mViewPagerAdapter
        mViewPager.setCurrentItem(index,false)
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                //已到最后，需要加载更多数据
                if(position==mViewPagerAdapter!!.itemCount-1){
                    val date=mStoryList!![position].date
                    mViewModel.beforeNewsList(date)
                }
                val story=mStoryList!![position]
                mStoryId=story.id
                mLongCount=0
                mShortCount=0
                mCount=0
                mTvMsgCount.text=""
                mTvLikeCount.text=""
                mViewModel.commentInfo(story.id)
                mViewModel.storyById(story.id)
            }

        })
    }

    override fun onBackPressed() {
        val intent=Intent()
        intent.putExtra("readList",mReadList)
        setResult(READ_LIST_CODE,intent)
        super.onBackPressed()
    }


    inner class StoryViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return mStoryList!!.size
        }

        override fun createFragment(position: Int): Fragment {
            val story=mStoryList!![position]
            return StoryDetailsFragment.newInstance(story)
        }

    }

}
