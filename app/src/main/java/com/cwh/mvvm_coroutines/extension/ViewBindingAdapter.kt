package com.cwh.mvvm_coroutines.extension

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cwh.mvvm_coroutines_base.base.binding.command.BindingCommand

/**
 * Description:
 * Date：2020/3/5-14:45
 * Author: cwh
 */

@BindingAdapter("android:onRefresh",requireAll = false)
fun onRefresh(refresh: SwipeRefreshLayout, command: BindingCommand){
    refresh.setOnRefreshListener {
        command.execute()
    }
}