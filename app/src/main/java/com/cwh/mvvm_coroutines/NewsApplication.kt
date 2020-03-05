package com.cwh.mvvm_coroutines

import com.cwh.mvvm_coroutines_base.BaseApplication
import com.facebook.stetho.Stetho

/**
 * Description:
 * Date：2020/2/29-14:36
 * Author: cwh
 */
class NewsApplication :BaseApplication(){
    override fun init() {
        Stetho.initializeWithDefaults(this)
    }

}