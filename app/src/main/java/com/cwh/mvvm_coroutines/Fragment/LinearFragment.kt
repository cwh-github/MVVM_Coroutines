package com.cwh.mvvm_coroutines.Fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwh.mvvm_base.base.ext.click
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines_base.base.adapter.BaseRecyclerViewAdapter
import com.cwh.mvvm_coroutines_base.base.adapter.SimpleLoadMoreView
import com.cwh.mvvm_coroutines_base.utils.DisplayUtils
import com.cwh.mvvm_coroutines_base.utils.LogUtils
import com.cwh.mvvm_coroutines.utils.GlideUtils
import kotlinx.android.synthetic.main.fragment_layout.*
import kotlinx.android.synthetic.main.item_recycler_type_one.view.*

/**
 * Description:
 * Date：2020/1/9 0009-20:03
 * Author: cwh
 */
class LinearFragment : Fragment() {

    private var mAdapter: MyLinearAdapter? = null

    private val mData = mutableListOf<String>()

    /**
     * 是否显示No More Data text
     */
    private var isNoMoreDataGone = false

    private lateinit var mTvText:TextView

    private lateinit var mImg1:ImageView

    private lateinit var mImg2:ImageView

    private lateinit var mImg3:ImageView

    private lateinit var mImg4:ImageView

    private lateinit var mImg5:ImageView

    private lateinit var mImg6:ImageView

    private val mHandelr=object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val color=msg.arg1
            mTvText.setBackgroundColor(color)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTvText=view.findViewById(R.id.mTvText)
        mImg1=view.findViewById(R.id.mImage1)
        mImg2=view.findViewById(R.id.mImage2)
        mImg3=view.findViewById(R.id.mImage3)
        mImg4=view.findViewById(R.id.mImage4)
        mImg5=view.findViewById(R.id.mImage5)
        mImg6=view.findViewById(R.id.mImage6)
        initRecyclerView()
        //initRecyclerView1()
        initBtnEvent()
        initRefresh()
        initColorPick()
    }

    private fun initColorPick() {
        GlideUtils.loadImageWithListener(context!!,
            "https://pic3.zhimg.com/v2-ff2dfa8f090102b31543cab51adf344a.jpg",mImg =mImage,
            onLoadSuccess={
                LogUtils.d("Drawable","Drawable is $it")
                it?.let {
                    val bitmap=DisplayUtils.drawableToBitmap(it)
                    mImage.post {
                        mImage.setImageBitmap(bitmap)
                    }
//                    Vibrant （有活力）
//                    Vibrant dark（有活力 暗色）
//                    Vibrant light（有活力 亮色）
//                    Muted （柔和）
//                    Muted dark（柔和 暗色）
//                    Muted light（柔和 亮色）
                    Palette.from(bitmap)
                        .generate {
                            LogUtils.d("Drawable","Pallette is $it")
                            LogUtils.d("Drawable","Pase Color is:${Color.parseColor("#ff0000")}")
                            val defaultColor=Color.parseColor("#ff0000")
                            LogUtils.d("Drawable","\n Vibrant: ${it?.getVibrantColor(defaultColor)}"+"\n"
                            +"Vibrant dark :${it?.getDarkVibrantColor(defaultColor)}"+"\n"+
                            "Vibrant light : ${it?.getLightVibrantColor(defaultColor)}"+"\n"+
                            "Muted: ${it?.getMutedColor(defaultColor)}"+"\n"+
                            "Muted dark: ${it?.getDarkMutedColor(defaultColor)}"+"\n"+
                            "Muted light: ${it?.getLightMutedColor(defaultColor)}")
                            val color=it?.mutedSwatch?.titleTextColor?:(Color.parseColor("#ff0000"))?:
                            Color.parseColor("#fffff0")
                            LogUtils.d("Drawable","Color is $color")
                            val msg=Message.obtain()
                            msg.arg1=color
                            //mHandelr.sendMessage(msg)

                            mImg1.post {

                                mImg1.setBackgroundColor(it!!.getVibrantColor(defaultColor))
                                mImg2.setBackgroundColor(it!!.getDarkVibrantColor(defaultColor))
                                mImg3.setBackgroundColor(it!!.getLightVibrantColor(defaultColor))
                                mImg4.setBackgroundColor(it!!.getMutedColor(defaultColor))
                                mImg5.setBackgroundColor(it!!.getDarkMutedColor(defaultColor))
                                mImg6.setBackgroundColor(it!!.getLightMutedColor(defaultColor))
                            }



                        }
                }


                true
            })

        mTvText.setBackgroundColor(-4144968)

    }




    private fun initRefresh() {
        mRefresh.setOnRefreshListener {
            mAdapter?.enableLoadMore(false)
            mHandler.postDelayed({
                val data = mutableListOf<String>()
                for (i in 0..10) {
                    data.add("$i")
                }
                if (mAdapter == null) {
                    initRecyclerView()
                    mAdapter!!.setNewData(data)
                } else {
                    mAdapter!!.setNewData(data)
                }
                mRefresh.isRefreshing = false
                mAdapter?.enableLoadMore(true)
            }, 2000)
        }
    }

    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {

        }
    }

    private fun initRecyclerView1() {
        if (mAdapter == null) {
            for (i in 0..30) {
                mData.add("$i")
            }
            mAdapter = MyLinearAdapter(this.activity!!, mData,false)
            val emptyView = View.inflate(this.activity!!, R.layout.empty_view, null)
            mAdapter!!.setEmptyView(emptyView)
            mAdapter!!.setLoadMoreView(SimpleLoadMoreView(this.activity!!))
            mAdapter!!.onLoadMoreListener = {
                LogUtils.d("BaseRecyclerViewAdapter", "On Load More")
                mHandler.postDelayed({
                    //是否加载数据成功
                    val num = java.util.Random().nextInt(12)
                    val result = num % 2 == 0
                    LogUtils.d("BaseRecyclerViewAdapter", "num is $num,reslut is $result")
                    if (result) {
                        val data = mutableListOf<String>()
                        for (i in 0..10) {
                            data.add("$i")
                        }
                        mAdapter!!.addData(data)
                        LogUtils.d("BaseRecyclerViewAdapter", "Add Data Success")
                        //是否还有更多数据
                        val result = java.util.Random().nextInt(10) > 6
                        if (result) {
                            mAdapter!!.loadMoreSuccess()
                        } else {
                            mAdapter!!.loadNoMoreData(isNoMoreDataGone)
                            LogUtils.d("BaseRecyclerViewAdapter", "No More Data")
                        }
                    } else {
                        mAdapter!!.loadFail()
                        LogUtils.d("BaseRecyclerViewAdapter", "Load Fail")
                    }

                }, 2000)
            }
            mRecyclerView.layoutManager = LinearLayoutManager(
                this.context, LinearLayoutManager.HORIZONTAL,
                false
            )
            mRecyclerView.adapter = mAdapter
            mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = dip2px(16f).toInt()
                }
            })
        }
    }

    private fun initRecyclerView() {
        if (mAdapter == null) {
            for (i in 0..30) {
                mData.add("$i")
            }
            mAdapter = MyLinearAdapter(this.activity!!, mData)
            val emptyView = View.inflate(this.activity!!, R.layout.empty_view, null)
            mAdapter!!.setEmptyView(emptyView)
            mAdapter!!.setLoadMoreView(SimpleLoadMoreView(this.activity!!))
            mAdapter!!.onLoadMoreListener = {
                LogUtils.d("BaseRecyclerViewAdapter", "On Load More")
                mHandler.postDelayed({
                    //是否加载数据成功
                    val num = java.util.Random().nextInt(12)
                    val result = num % 2 == 0
                    LogUtils.d("BaseRecyclerViewAdapter", "num is $num,reslut is $result")
                    if (result) {
                        val data = mutableListOf<String>()
                        for (i in 0..10) {
                            data.add("$i")
                        }
                        mAdapter!!.addData(data)
                        LogUtils.d("BaseRecyclerViewAdapter", "Add Data Success")
                        //是否还有更多数据
                        val result = java.util.Random().nextInt(10) > 6
                        if (result) {
                            mAdapter!!.loadMoreSuccess()
                        } else {
                            mAdapter!!.loadNoMoreData(isNoMoreDataGone)
                            LogUtils.d("BaseRecyclerViewAdapter", "No More Data")
                        }
                    } else {
                        mAdapter!!.loadFail()
                        LogUtils.d("BaseRecyclerViewAdapter", "Load Fail")
                    }

                }, 2000)
            }
            mRecyclerView.layoutManager = LinearLayoutManager(
                this.context, LinearLayoutManager.VERTICAL,
                false
            )
            mRecyclerView.adapter = mAdapter
            mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = dip2px(16f).toInt()
                }
            })
        }

    }

    private fun initBtnEvent() {
        mBtnAddHead.click {
            val view = LayoutInflater.from(this.activity!!)
                .inflate(R.layout.item_recycler_type_one, mRecyclerView, false)
            view.mTvText.text = "Header View"
            mAdapter!!.addHeaderView(view)
        }

        mBtnRemoveHead.click {
            if (mAdapter!!.hasHeaderView()) {
                mAdapter!!.removeHeaderView(0)
            }
        }

        mBtnAddFooter.click {
            val view = LayoutInflater.from(this.activity!!)
                .inflate(R.layout.item_recycler_type_one, mRecyclerView, false)
            view.mTvText.text = "Footer View"
            mAdapter!!.addFooterView(view)
        }

        mBtnRemoveFooter.click {
            if (mAdapter!!.hasFooterView()) {
                mAdapter!!.removeFooterView(0)
            }
        }
        mBtnEnableLoadMore.click {
            mAdapter!!.enableLoadMore(true)
        }

        mBtnNoEnableLoadMore.click {
            mAdapter!!.enableLoadMore(false)
        }

        mBtnEmptyView.click {
            val datas = mutableListOf<String>()
            mAdapter!!.setNewData(datas, true)
            mAdapter!!.mEmptyViewWithFooterView = true
            mAdapter!!.mEmptyViewWithHeaderView = true
        }

        mBtnNoLoadMore.click {
            isNoMoreDataGone = true
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dip2px(dpValue: Float): Float {
        val scale = context!!.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

}


class MyLinearAdapter(mContext: Context, mData: MutableList<String>,val isVer:Boolean=true) :
    BaseRecyclerViewAdapter<String>(mContext, mData) {
    val TYPE_ONE = 0
    val TYPE_TWO = 2

    override fun getContentViewItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            TYPE_ONE
        } else {
            TYPE_TWO
        }
    }

    override fun onCreateContentViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ONE) {
            val view =if(isVer)
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_type_one, parent, false)
            else
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_type_horization_one, parent, false)
            LinearViewHolder(view)
        } else {
            val view =if(isVer)
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_type_two, parent, false)
            else
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_type_horization_two, parent, false)
            LinearViewHolder1(view)
        }


    }

    override fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = holder.itemViewType
        val mViewHolder = if (type == TYPE_ONE) holder as LinearViewHolder
        else holder as LinearViewHolder1
        mViewHolder.itemView.mTvText.text = "Position is $position"
    }

    inner class LinearViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class LinearViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView)

}