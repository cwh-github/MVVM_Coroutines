package com.cwh.mvvm_coroutines.ui.details

import android.content.Intent
import android.graphics.Bitmap
import android.webkit.*
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.cwh.mvvm_coroutines.BR
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.databinding.StoryDetailsBinding
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.ui.comment.CommentActivity
import com.cwh.mvvm_coroutines.utils.WebViewUtils
import com.cwh.mvvm_coroutines_base.base.click
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import com.cwh.mvvm_coroutines_base.base.viewmodel.NoViewModel
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import com.cwh.mvvm_coroutines_base.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_story_details.*

class StoryDetailsActivity : BaseActivity<StoryDetailsViewModel,StoryDetailsBinding>() {
    override val mViewModel: StoryDetailsViewModel
        by lazy { createViewModel(this,StoryDetailsViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_story_details
    override val mViewModelVariableId: Int
        get() = BR.storyDetailsViewModel

    private var mStory:Story?=null

    private var id:Long?=null
    private var mCount=-1
    private var mLongCount=0
    private var mShortCount=0

    override fun initDataAndView() {
        initClickListener()
        mStory=intent.getSerializableExtra("story") as Story?
        mStory?.let {
            id=it.id
            mViewModel.commentInfo(it.id)
            showDetails(it)
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
    }

    private fun initClickListener() {
        mImgBack.click {
            finish()
        }

        mRelMsg.click {
            LogUtils.d("Msg","$mCount  $id")
                if(mCount!=-1){
                    id?.let {
                        startActivity(Intent(this,CommentActivity::class.java)
                            .putExtra("id",it)
                            .putExtra("count",mCount)
                            .putExtra("mLongCount",mLongCount)
                            .putExtra("mShortCount",mShortCount)
                        )
                    }
                }


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
