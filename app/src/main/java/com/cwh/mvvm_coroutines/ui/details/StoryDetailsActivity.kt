package com.cwh.mvvm_coroutines.ui.details

import android.graphics.Bitmap
import android.webkit.*
import androidx.databinding.ViewDataBinding
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.utils.WebViewUtils
import com.cwh.mvvm_coroutines_base.base.click
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import com.cwh.mvvm_coroutines_base.base.viewmodel.NoViewModel
import kotlinx.android.synthetic.main.activity_story_details.*

class StoryDetailsActivity : BaseActivity<NoViewModel,ViewDataBinding>() {
    override val mViewModel: NoViewModel
        by lazy { createViewModel(this,NoViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_story_details
    override val mViewModelVariableId: Int
        get() = -1

    private var mStory:Story?=null

    override fun initDataAndView() {
        initClickListener()
        mStory=intent.getSerializableExtra("story") as Story?
        mStory?.let {
            showDetails(it)
        }

    }

    private fun initClickListener() {
        mImgBack.click {
            finish()
        }

        mRelMsg.click {

        }

        mRelLike.click {

        }

        mRelCollect.click {

        }

        mRelShare.click{


        }
    }

    private fun showDetails(story: Story) {
        WebViewUtils.webViewSetting(mWebView)
        mRefresh.isRefreshing=true
        mRefresh.setOnRefreshListener {
            mWebView.reload()
        }
        mWebView.loadUrl(story.url)
        mWebView.webChromeClient=object : WebChromeClient() {


        }

        mWebView.webViewClient=object :WebViewClient(){

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mRefresh.isRefreshing=true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mRefresh.isRefreshing=false
            }


        }
    }
}
