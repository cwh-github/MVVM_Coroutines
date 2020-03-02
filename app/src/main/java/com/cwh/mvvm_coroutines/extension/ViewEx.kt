package com.cwh.mvvm_coroutines.extension

import android.graphics.Color

/**
 * Description:
 * Date：2020/3/2-17:07
 * Author: cwh
 */

/**
 * @param hex  0xb3947d
 */
fun String.parseHex():Int{
    return Color.parseColor(this.replace("0x","#",true))
}

fun Int.rangeTo(start:Int,end:Int):Boolean{
    return this in start..end
}