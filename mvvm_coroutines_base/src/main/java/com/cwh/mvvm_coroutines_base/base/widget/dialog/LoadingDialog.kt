package com.cwh.mvvm_coroutines_base.base.widget.dialog

import android.app.Dialog
import android.content.Context
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import com.cwh.mvvm_coroutines_base.R
import com.cwh.mvvm_coroutines_base.base.ext.inflate
import com.cwh.mvvm_coroutines_base.utils.DisplayUtils

/**
 * Description:延迟显示和隐藏的加载对话框的一种示例
 *
 * 在加载数据时间小于delayShowTime时，不显示加载对话框，直接渲染UI,避免加载过快
 * 对话框闪一下消失
 *
 * 在加载数据大于delayShowTime 但对话框的显示时间小于delayHideTime时，强行显示
 * delayHideTime后消失，避免对话框显示出来后，马上消失，造成闪一下的效果
 *
 * 此思想可以用于任何加载过渡的UI，这里只是以显示dialog为例
 *
 * Date：2020/6/28 0028-15:10
 * Author: cwh
 *
 * @param mMinDelayTime 最小延迟多长时间后显示
 * @param mMinShowTime 显示出对话框后，最下显示的时长
 */
class LoadingDialog(
    context: Context,
    private val mMinDelayTime: Long = 500,
    private val mMinShowTime: Long = 500
) {

    init {
        createDialog(context)
    }

    //开始展示对话框的时间点
    private var mStartShowTime = -1L

    //是否延迟显示
    private var mPostShow = false

    //是否延迟隐藏
    private var mPostHide = false

    //是否隐藏了
    private var mDismissed = false

    private val mHandler = android.os.Handler(Looper.getMainLooper())

    private val mDelayShowRunnable = Runnable {
        mPostShow = false
        if (!mDismissed) {
            mStartShowTime = System.currentTimeMillis()
            if (!mDialog.isShowing) {
                mDialog.show()
            }
        }
    }

    private val mDelayHideRunnable = Runnable {
        mPostHide = false
        mStartShowTime = -1L
        if (mDialog.isShowing) {
            mDialog.dismiss()
        }
    }

    private lateinit var mDialog: Dialog
    private fun createDialog(context: Context) {
        mDialog = Dialog(context)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = context.inflate(R.layout.layout_loading_dialog, null)
        mDialog.setContentView(view)
        val window: Window? = mDialog.window
        //要加上设置背景，否则dialog宽高设置无作用
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window?.attributes
        layoutParams?.let {
            layoutParams.width = DisplayUtils.getDeviceWidthAndHeight()[0] -
                    2 * DisplayUtils.dip2px(context, 60f)
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        window?.attributes = layoutParams
    }

    /**
     * 延迟show,在经过指定时间后，还需要显示(handler队列中还有要显示对话框的Runnable)则显示出来
     */
    fun show() {
        mStartShowTime = -1
        mDismissed = false
        mHandler.removeCallbacks(mDelayHideRunnable)
        mPostHide = false
        if (!mPostShow) {
            mPostShow = true
            mHandler.postDelayed(mDelayShowRunnable, mMinDelayTime)
        }
    }

    /**
     * 延迟hide,在经过指定时间后，隐藏对话框
     * 如果对话框已显示出来且小于最小显示时间，则延迟到指定时间后隐藏
     * 如果显示时间大于最小显示时间或为显示过，则隐藏
     */
    fun hide() {
        mDismissed = true
        mHandler.removeCallbacks(mDelayShowRunnable)
        mPostShow = false
        val haveShowTime = System.currentTimeMillis() - mStartShowTime
        if (haveShowTime > mMinDelayTime || mStartShowTime == -1L) {
            mDialog.dismiss()
        } else {
            if (!mPostHide) {
                mPostHide = true
                mHandler.postDelayed(mDelayHideRunnable, mMinShowTime - haveShowTime)
            }
        }
    }

}