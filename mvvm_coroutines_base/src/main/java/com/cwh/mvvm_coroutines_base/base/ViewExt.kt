package com.cwh.mvvm_coroutines_base.base

import android.view.View
import androidx.annotation.IdRes
import kotlin.math.abs

/**
 * Description:View扩展函数，便于一些方法的调用
 * Date：2019/7/15-12:27
 * Author: cwh
 */

/**
 * findViewById 的简写
 */
fun <T : View> View.find(@IdRes id: Int): T =
    this.findViewById(id)


/**
 * 上次点击事件的时间(不要对该值进行赋值操作)
 *
 *
 * */
var lastClickTime: Long = 0

/**
 * View添加防重复点击的clickListener 事件
 * @param time 指定时间间隔内为重复点击，默认500毫秒
 *
 */
fun View.click(time: Long = 500,listener: (View) -> Unit) {
    this.setOnClickListener {
        if (abs(System.currentTimeMillis() - lastClickTime) > time) {
            listener(this)
            lastClickTime = System.currentTimeMillis()
        }
    }
}




