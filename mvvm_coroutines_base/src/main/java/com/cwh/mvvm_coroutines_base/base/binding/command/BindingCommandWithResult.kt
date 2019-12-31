package com.cwh.mvvm_coroutines_base.base.binding.command

/**
 * Description:返回结果参数
 * Date：2019/12/31 0031-17:29
 * Author: cwh
 */
class BindingCommandWithResult<T, R> {

    private var action: BindingFunction<R>? = null

    private var function: Function<T, R>? = null

    private var canExecute: BindingFunction<Boolean>? = null


    constructor(action: BindingFunction<R>?) {
        this.action = action
    }

    constructor(function: Function<T, R>?) {
        this.function = function
    }

    constructor(action: BindingFunction<R>?, canExecute: BindingFunction<Boolean>?) {
        this.action = action
        this.canExecute = canExecute
    }

    constructor(function: Function<T, R>?, canExecute: BindingFunction<Boolean>?) {
        this.function = function
        this.canExecute = canExecute
    }


    private fun canExecute(): Boolean {
        return if (canExecute != null) {
            canExecute!!.call()
        } else true

    }

    fun execute(): R? {
        return if (action != null && canExecute()) {
            action!!.call()
        } else null
    }


    fun execute(params: T): R? {
        return if (function != null && canExecute()) {
            function!!.call(params)
        } else null
    }


}