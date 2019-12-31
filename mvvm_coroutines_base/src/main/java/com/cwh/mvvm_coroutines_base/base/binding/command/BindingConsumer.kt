package com.cwh.mvvm_coroutines_base.base.binding.command

/**
 * Description:需要参数，不需要返回结果
 * Date：2019/12/31 0031-16:54
 * Author: cwh
 */
interface BindingConsumer<T> {
    fun call(t:T)
}