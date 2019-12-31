package com.cwh.mvvm_coroutines_base.base.binding.command

/**
 * Description:需要参数，不会返回结果
 * Date：2019/12/31 0031-17:17
 * Author: cwh
 */
class BindingCommandWithParams<T> {

    private var action:BindingConsumer<T>?=null

    private var canExecute:BindingFunction<Boolean>?=null

    constructor(action: BindingConsumer<T>?) {
        this.action = action
    }

    constructor(action: BindingConsumer<T>?, canExecute: BindingFunction<Boolean>?) {
        this.action = action
        this.canExecute = canExecute
    }


    private fun canExecute():Boolean{
        return if(canExecute==null){
            true
        }else canExecute!!.call()
    }


    fun execute(params:T){
        if(action!=null && canExecute()){
            action!!.call(params)
        }
    }


}