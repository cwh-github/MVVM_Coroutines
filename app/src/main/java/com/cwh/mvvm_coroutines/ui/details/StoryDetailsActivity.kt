package com.cwh.mvvm_coroutines.ui.details

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import com.cwh.mvvm_coroutines_base.base.ext.click
import com.cwh.mvvm_coroutines.BR
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.databinding.StoryDetailsBinding
import com.cwh.mvvm_coroutines.extension.parseHex
import com.cwh.mvvm_coroutines.model.NewsDetails
import com.cwh.mvvm_coroutines.model.Story
import com.cwh.mvvm_coroutines.ui.comment.CommentActivity
import com.cwh.mvvm_coroutines.utils.GlideUtils
import com.cwh.mvvm_coroutines.utils.WebViewUtils
import com.cwh.mvvm_coroutines_base.base.Status
import com.cwh.mvvm_coroutines_base.base.observerEvent
import com.cwh.mvvm_coroutines_base.base.view.BaseActivity
import com.cwh.mvvm_coroutines_base.utils.DisplayUtils
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import com.cwh.mvvm_coroutines_base.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_story_details.*

class StoryDetailsActivity : BaseActivity<StoryDetailsViewModel, StoryDetailsBinding>() {
    override val mViewModel: StoryDetailsViewModel
            by lazy { createViewModel(this, StoryDetailsViewModel::class.java) }
    override val layoutId: Int
        get() = R.layout.activity_story_details
    override val mViewModelVariableId: Int
        get() = BR.storyDetailsViewModel

    private var mStory: Story? = null

    private var id: Long? = null
    private var mCount = -1
    private var mLongCount = 0
    private var mShortCount = 0

    private var isTran: Boolean = true

    override fun initDataAndView() {
        //设置状态栏透明
        val decorView = window.decorView
        var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT


        initClickListener()
        mStory = intent.getSerializableExtra("story") as Story?
        mStory?.let {
            id = it.id
            mViewModel.commentInfo(it.id)
            mViewModel.storyById(id!!)
            mViewModel.newsDetails(id!!)
        }
        mScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener
        { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (oldScrollY >= mImageTop.bottom) {
                if (isTran) {
                    //设置状态栏白色
                    val decorView = window.decorView
                    var option = decorView.systemUiVisibility and
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    decorView.systemUiVisibility = option
                    window.statusBarColor = Color.WHITE
                    isTran = false
                }
            } else {
                if (!isTran) {
                    //设置状态栏透明
                    val decorView = window.decorView
                    var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    decorView.systemUiVisibility = option
                    window.statusBarColor = Color.TRANSPARENT
                    isTran = true
                }

            }
        })

    }

    override fun initViewObserver() {
        mViewModel.toastMsg.observe(this, Observer {
            ToastUtils.showToast(this, it)
        })

        mViewModel.commentCount.observe(this, Observer {
            mCount = it
        })

        mViewModel.mCommentInfo.observe(this, Observer {
            mLongCount = it.long_comments
            mShortCount = it.short_comments
        })

        mViewModel.mStory.observerEvent(this) {
            if (it.isLike) {
                mImgCollect.setImageResource(R.drawable.ic_collect_sel)
            } else {
                mImgCollect.setImageResource(R.drawable.ic_collect)
            }
        }

        mViewModel.isLike.observerEvent(this) {
            if (it) {
                mImgCollect.setImageResource(R.drawable.ic_collect_sel)
            } else {
                mImgCollect.setImageResource(R.drawable.ic_collect)
            }
        }

        mViewModel.mDetails.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> mRefresh.isRefreshing = true
                Status.ERROR -> mRefresh.isRefreshing = false
                Status.SUCCESS -> {
                    if (it.data == null) {
                        mRefresh.isRefreshing = false
                    } else {
                        showDetails(it.data!!)
                        mRefresh.isRefreshing = false
                    }

                }

            }
        })
    }

    private fun initClickListener() {
        mImgBack.click {
            finish()
        }

        mRelMsg.click {
            LogUtils.d("Msg", "$mCount  $id")
            if (mCount != -1) {
                id?.let {
                    startActivity(
                        Intent(this, CommentActivity::class.java)
                            .putExtra("id", it)
                            .putExtra("count", mCount)
                            .putExtra("mLongCount", mLongCount)
                            .putExtra("mShortCount", mShortCount)
                    )
                }
            }


        }

        mRelLike.click {
            ToastUtils.showToast(this, "并没有卵用 (ノ—_—)ノ~┴————┴ ")
        }

        mRelCollect.click {
            id?.let {
                mViewModel.likeOrUnLikeStory(id!!)
            }
        }

        mRelShare.click {
            if (mStory == null) {
                ToastUtils.showToast(this, "并没有卵用 (ノ—_—)ノ~┴————┴ ")
            } else {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, mStory!!.url)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "分享"))
            }

        }
    }

    private fun showDetails(details: NewsDetails) {
        GlideUtils.loadImage(this, details.image, mImg = mImageTop)
        //设置渐变色
        val endColor =
            mStory?.image_hue?.parseHex() ?: resources.getColor(R.color.default_bot_color)
        val startColor = Color.TRANSPARENT
        mViewBg2.setBackgroundColor(endColor)
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(endColor, startColor)
        )
        mViewBg.background = gradientDrawable

        mTvTips.text = details.imageTips ?: ""
        mTvTitle.text = details.title ?: ""
        if (details.questionTitle.isNullOrEmpty()) {
            mTvAnswerTitle.isVisible = false
        } else {
            mTvAnswerTitle.isVisible = true
            mTvAnswerTitle.text = details.questionTitle ?: ""
        }

        GlideUtils.loadRoundImage(
            this, details.authorImage ?: "",
            radius = DisplayUtils.dip2px(this, 2f), mImg = mImgAuthor
        )

        mTvAuthor.text = "作者/${details.author.replace("，", "")}"

        WebViewUtils.webViewSetting(mWebView)
        mRefresh.setOnRefreshListener {
            if (id != null) {
                mViewModel.newsDetails(id!!)
            } else {
                mRefresh.isRefreshing = false
            }

        }
        mWebView.loadDataWithBaseURL("", details.content, "text/html", "utf-8", null)
    }

}
