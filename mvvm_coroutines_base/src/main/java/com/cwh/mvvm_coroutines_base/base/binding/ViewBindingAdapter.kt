package com.cwh.mvvm_coroutines_base.base.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingCommand
import com.cwh.mvvm_coroutines_base.base.click

/**
 * Description:
 * Date：2019/12/31 0031-16:42
 * Author: cwh
 */

/**
 * 定义View的click事件，添加了防多次点击的方法
 */
@BindingAdapter("onClickCommand",requireAll = false)
fun onClickCommand(view: View,command: BindingCommand){
    view.click {
        command.execute()
    }
}