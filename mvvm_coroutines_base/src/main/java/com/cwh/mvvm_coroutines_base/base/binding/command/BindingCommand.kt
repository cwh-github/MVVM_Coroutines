package com.cwh.mvvm_coroutines_base.base.binding.command

import com.cwh.mvvm_coroutines_base.utils.LogUtils

/**
 * Description:不需要参数，也不会返回结果
 * Date：2019/12/31 0031-17:08
 * Author: cwh
 */
class BindingCommand {
    private var action:BindingAction?=null

    private var canExecute:BindingFunction<Boolean>?=null

    constructor(action: BindingAction?) {
        this.action = action
    }

    constructor(action: BindingAction?, function: BindingFunction<Boolean>?) {
        this.action = action
        this.canExecute = function
    }


    private fun canExecute():Boolean{
        return if(canExecute==null){
            true
        }else canExecute!!.call()
    }

    fun execute(){
        LogUtils.d("Command","Call Excute")
        if(action!=null && canExecute()){
            action!!.call()
        }
    }


}