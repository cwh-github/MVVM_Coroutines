package com.cwh.mvvm_coroutines_base.base

/**
 * Description:
 * Date：2020/1/2 0002-14:22
 * Author: cwh
 */

open class Entity<T>(val msg:String,val code:Int,var data:T){

    override fun toString(): String {
        return "Entity(msg='$msg', code=$code, data=$data)"
    }
}