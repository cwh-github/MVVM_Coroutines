package com.cwh.mvvm_coroutines.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines.extension.parseHex
import com.cwh.mvvm_coroutines.model.TopStory
import com.cwh.mvvm_coroutines_base.base.find
import com.cwh.mvvm_coroutines_base.utils.DisplayUtils
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import com.cwh.mvvm_coroutines.utils.GlideUtils

/**
 * Description:
 * Date：2020/3/2-16:30
 * Author: cwh
 */
class NewsHeadView(val context: Context,val news:MutableList<TopStory>) {

    lateinit var mRootView: View
    private lateinit var mViewPager:ViewPager2
    private lateinit var mContainer:LinearLayout

    private val mIndicators:MutableList<View> = mutableListOf()

    //指示点对应的最小宽度
    private val mSmallWidth=DisplayUtils.dip2px(context,4f)

    private val mSpace=DisplayUtils.dip2px(context,5f)

    private val mHeight=DisplayUtils.dip2px(context,4f)

    //指示点对应的最大宽度
    private val mBigWidth=DisplayUtils.dip2px(context,18f)

    private val mDistance=mBigWidth-mSmallWidth

    /**
     * 0 向左滑，1 向右滑
     */
    private var mDirection=-1


    private val LEFT=0

    private val RIGHT=1

    private var mSelPosition=0

    private val START_LOOP=10001

    private var isLoop=false

    private val mHandler= object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if(msg.what==START_LOOP){
                if(isLoop){
                    mViewPager.currentItem=mViewPager.currentItem+1
                    sendEmptyMessageDelayed(START_LOOP,5000)
                }

            }
        }
    }

    init {
        createView()
    }

    /**
     * 开始循环
     */
    fun startLoop(){
        if(!isLoop){
            isLoop=true
            mHandler.sendEmptyMessageDelayed(START_LOOP,5000)
        }
    }

    /**
     * 停止循环
     */
    fun stopLoop(){
        isLoop=false
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun createView() {
        mRootView=View.inflate(context, R.layout.item_news_header,null)
        mViewPager=mRootView.find(R.id.mViewPager)
        mContainer=mRootView.find(R.id.mPointContainer)
        initContainer()
        initViewPager()
    }

    private fun initContainer() {
        if(news.size>1){
            news.forEachIndexed { index, _ ->
                val view=View(context)
                if(index==0){
                    val params=LinearLayout.LayoutParams(mBigWidth,mHeight)
                    view.setBackgroundResource(R.drawable.view_pager_indicator)
                    params.leftMargin=mSpace
                    view.layoutParams=params
                }else{
                    val params=LinearLayout.LayoutParams(mSmallWidth,mHeight)
                    view.setBackgroundResource(R.drawable.view_pager_indicator)
                    params.leftMargin=mSpace
                    view.layoutParams=params
                    view.alpha=0.5f
                }
                mContainer.addView(view)
                mIndicators.add(view)
            }
        }
    }

    private fun initViewPager() {
        mViewPager.adapter=object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return ViewPagerViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_pager_item,parent,false))
            }

            override fun getItemCount()=if(news.isNotEmpty()){
                Int.MAX_VALUE
            }else{
                0
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val newsSize=news.size
                if(newsSize>0){
                    val realPosition=position%newsSize
                    val news=news[realPosition]
                    val mViewHolder=holder as ViewPagerViewHolder
                    GlideUtils.loadImage(holder.itemView.context,news.image,mImg=mViewHolder.mImage)
                    mViewHolder.mTvAuthor.text=news.hint?:""
                    mViewHolder.mTvTitle.text=news.title
                    val endColor=news.image_hue?.parseHex()?:
                    context.resources.getColor(R.color.default_bot_color)
                    val startColor=Color.TRANSPARENT
                    mViewHolder.mViewBg2.setBackgroundColor(endColor)
                    val gradientDrawable=GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(endColor,startColor))
                    mViewHolder.mViewBg.background=gradientDrawable
                }

            }
        }

        mViewPager.setCurrentItem(1000*news.size,false)


        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
//                LogUtils.d("ViewPager","position Now is $position")
//                LogUtils.d("ViewPager","position is ${position%news.size}")
//                LogUtils.d("ViewPager","positionOffset : $positionOffset")
//                LogUtils.d("ViewPager","positionOffsetPixels : $positionOffsetPixels")
                //现在选中的position
                var selViewPosition=position%news.size
                if(positionOffset<=0.1 && positionOffset>0 && mDirection<0){
                    mDirection=LEFT
                }
                if(positionOffset>=0.9 && positionOffset<1 && mDirection<0){
                    mDirection=RIGHT
                }
                if(positionOffset==0f){
                    mDirection=-1
                }
                LogUtils.d("ViewPager","mDirection : $mDirection")
                if(mDirection>=0){
                    val unSelViewPosition=if(mDirection==LEFT){
                        //向左滑时，在没滑到前，position未加1
                        (position+1)%news.size
                    }else{
                        selViewPosition=(position+1)%news.size
                        //向右滑动时，在没滑到前，position已减1
                        (position)%news.size
                    }
                    if(mDirection==LEFT){
                        val selWidth=mSmallWidth+mDistance*(1-positionOffset)
                        val unSelWidth=mSmallWidth+mDistance*positionOffset
                        val params= mIndicators[selViewPosition].layoutParams
                        params.width=selWidth.toInt()
                        mIndicators[selViewPosition].layoutParams=params
                        val unSelParams=mIndicators[unSelViewPosition].layoutParams
                        unSelParams.width=unSelWidth.toInt()
                        mIndicators[unSelViewPosition].layoutParams=unSelParams
                        mIndicators[selViewPosition].alpha=0.5f+0.5f*(1-positionOffset)
                        mIndicators[unSelViewPosition].alpha=0.5f+0.5f*positionOffset
                    }else{
                        val selWidth=mSmallWidth+mDistance*positionOffset
                        val unSelWidth=mSmallWidth+mDistance*(1-positionOffset)
                        val params= mIndicators[selViewPosition].layoutParams
                        params.width=selWidth.toInt()
                        mIndicators[selViewPosition].layoutParams=params
                        val unSelParams= mIndicators[unSelViewPosition].layoutParams
                        unSelParams.width=unSelWidth.toInt()
                        mIndicators[unSelViewPosition].layoutParams=unSelParams

                        mIndicators[selViewPosition].alpha=0.5f+0.5f*positionOffset
                        mIndicators[unSelViewPosition].alpha=0.5f+0.5f*(1-positionOffset)
                    }

                }


            }

            override fun onPageSelected(position: Int) {
                mSelPosition=position

            }

            override fun onPageScrollStateChanged(state: Int) {
                //SCROLL_STATE_IDLE==空闲状态
                //SCROLL_STATE_DRAGGING==正在滑动
                //SCROLL_STATE_SETTLING==自然沉降
                if(state==SCROLL_STATE_IDLE){
                    val realPosition=mSelPosition%news.size
                    mIndicators.forEachIndexed{index,view->
                        if(index==realPosition){
                            view.layoutParams.width=mBigWidth
                            view.alpha=1.0f
                        }else{
                            view.layoutParams.width=mSmallWidth
                            view.alpha=0.5f
                        }
                    }
                    //mDirection=-1
                }
            }
        })

    }

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mImage=itemView.find<ImageView>(R.id.mImageTop)
        val mViewBg=itemView.find<View>(R.id.mViewBg)
        val mTvAuthor=itemView.find<TextView>(R.id.mTvAuthor)
        val mTvTitle=itemView.find<TextView>(R.id.mTvTitle)
        val mViewBg2=itemView.find<View>(R.id.mViewBg2)
    }


}