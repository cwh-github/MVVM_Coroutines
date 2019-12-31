package com.cwh.mvvm_coroutines_base.base.binding.command

/**
 * Description:不需要参数，但返回参数
 * Date：2019/12/31 0031-16:55
 * Author: cwh
 */
interface BindingFunction<T>{

    fun call():T
}