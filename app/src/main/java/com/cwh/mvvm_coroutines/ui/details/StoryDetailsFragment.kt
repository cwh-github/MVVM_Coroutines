package com.cwh.mvvm_coroutines.ui.details

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import com.cwh.mvvm_coroutines.BR
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.databinding.StoryDetailsFragmentBinding
import com.cwh.mvvm_coroutines.extension.parseHex
import com.cwh.mvvm_coroutines.model.NewsDetails
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.utils.GlideUtils
import com.cwh.mvvm_coroutines.utils.WebViewUtils
import com.cwh.mvvm_coroutines_base.base.Status
import com.cwh.mvvm_coroutines_base.base.view.BaseFragment
import com.cwh.mvvm_coroutines_base.utils.DisplayUtils
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import com.cwh.mvvm_coroutines_base.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_story_details.*

/**
 * Description:
 * Date：2020/3/9-13:38
 * Author: cwh
 */
class StoryDetailsFragment private constructor() :
    BaseFragment<StoryDetailsViewModel, StoryDetailsFragmentBinding>() {
    override val mViewModel: StoryDetailsViewModel
            by lazy { createViewModel(this, StoryDetailsViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.fragment_story_details
    override val mViewModelVariableId: Int
        get() = BR.fragmentStoryDetails

    private var mStory: Story? = null

    private var mStoryId: Long? = null

    private var isTran: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStory = arguments?.getSerializable("story") as? Story
        mStory?.let {
            mStoryId=it.id
            mViewModel.setStoryIsRead(it)
        }
    }

    override fun initDataAndView() {
        mStory?.let {
            mViewModel.newsDetails(mStoryId!!)
        }
        mScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener
            { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (oldScrollY >= mImageTop.bottom) {
                    if (isTran) {
                        //设置状态栏白色
                        val decorView = mActivity.window.decorView
                        var option = decorView.systemUiVisibility and
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        decorView.systemUiVisibility = option
                        mActivity.window.statusBarColor = Color.WHITE
                        isTran = false
                    }
                } else {
                    if (!isTran) {
                        //设置状态栏透明
                        val decorView = mActivity.window.decorView
                        var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        decorView.systemUiVisibility = option
                        mActivity.window.statusBarColor = Color.TRANSPARENT
                        isTran = true
                    }

                }
            })
    }

    override fun onResume() {
        super.onResume()
        if(isTran){
            //设置状态栏透明
            val decorView = mActivity.window.decorView
            var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            mActivity.window.statusBarColor = Color.TRANSPARENT
        }else{
            //设置状态栏白色
            val decorView = mActivity.window.decorView
            var option = decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            mActivity.window.statusBarColor = Color.WHITE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.d("Fragment","onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("Fragment","onDestroy")
    }

    override fun initViewObserver() {
        mViewModel.toastMsg.observe(this, Observer {
            ToastUtils.showToast(mActivity, it)
        })

        mViewModel.mDetails.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> mRefresh.isRefreshing = true
                Status.ERROR -> mRefresh.isRefreshing = false
                Status.SUCCESS -> {
                    it.data?.let {
                        showDetails(it)
                    }
                    mRefresh.isRefreshing = false
                }

            }
        })
    }

    private fun showDetails(details: NewsDetails) {
        GlideUtils.loadImage(mActivity,details.image,mImg = mImageTop)
        //设置渐变色
        val endColor=mStory?.image_hue?.parseHex()?:
        resources.getColor(R.color.default_bot_color)
        val startColor= Color.TRANSPARENT
        mViewBg2.setBackgroundColor(endColor)
        val gradientDrawable= GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(endColor,startColor))
        mViewBg.background=gradientDrawable

        mTvTips.text=details.imageTips?:""
        mTvTitle.text=details.title?:""
        if(details.questionTitle.isNullOrEmpty()){
            mTvAnswerTitle.isVisible=false
        }else {
            mTvAnswerTitle.text = details.questionTitle ?: ""
        }

        GlideUtils.loadRoundImage(mActivity,details.authorImage?:"",
            radius = DisplayUtils.dip2px(mActivity,2f),mImg = mImgAuthor)

        mTvAuthor.text=details.author

        WebViewUtils.webViewSetting(mWebView)
        mRefresh.setOnRefreshListener {
            if(mStoryId!=null){
                mViewModel.newsDetails(mStoryId!!)
            }else{
                mRefresh.isRefreshing=false
            }

        }
        mWebView.loadDataWithBaseURL("",details.content, "text/html",
            "utf-8", null)
    }


    companion object {
        fun newInstance(story: Story): StoryDetailsFragment {
            val fragment = StoryDetailsFragment()
            val bundle = Bundle()
            bundle.putSerializable("story", story)
            fragment.arguments = bundle
            return fragment
        }

    }


}