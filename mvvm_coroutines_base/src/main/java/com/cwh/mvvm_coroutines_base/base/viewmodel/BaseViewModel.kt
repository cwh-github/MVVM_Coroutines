package com.cwh.mvvm_coroutines_base.base.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cwh.mvvm_coroutines_base.base.repository.IRepository

/**
 * Description:BaseViewModel 主要的数据逻辑操作在ViewModel中执行
 * 通过LiveData 或者 DataBinding驱动数据绑定到UI
 *
 *
 * Date：2019/12/31 0031-15:36
 * Author: cwh
 */
abstract class BaseViewModel<T:IRepository>(application: Application) : AndroidViewModel(application)
    ,IBaseViewModel {

    abstract var repo:T








    override fun onAny() {
    }

    override fun onCreate() {
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPauese() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }
}