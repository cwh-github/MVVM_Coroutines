package com.cwh.mvvm_coroutines_base.base.viewmodel

import android.app.Application
import com.cwh.mvvm_coroutines_base.base.repository.IRepository

/**
 * Description:不需要ViewModel时，
 * 传入该类型ViewModel即可(但是还是拥有一些基本的ViewModel功能，
 * 如跳转界面等)
 * Date：2020/1/2 0002-14:00
 * Author: cwh
 */
class NoViewModel(application: Application) : BaseViewModel<IRepository>(application) {
    override var repo: IRepository = object : IRepository {
        override fun onClear() {

        }
    }


}