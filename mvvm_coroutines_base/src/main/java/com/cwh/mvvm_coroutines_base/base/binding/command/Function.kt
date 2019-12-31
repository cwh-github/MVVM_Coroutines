package com.cwh.mvvm_coroutines_base.base.binding.command

/**
 * Description:需要参数和返回结果
 * Date：2019/12/31 0031-16:58
 * Author: cwh
 */
interface Function<T, R> {
    fun call(t: T): R
}