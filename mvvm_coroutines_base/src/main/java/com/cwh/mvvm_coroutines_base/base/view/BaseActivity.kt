package com.cwh.mvvm_coroutines_base.base.view

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.cwh.mvvm_coroutines_base.base.viewmodel.BaseViewModel

/**
 * Description:
 * Date：2019/12/31 0031-14:58
 * Author: cwh
 */
abstract class BaseActivity<VM:BaseViewModel<*>,V:ViewDataBinding>: AppCompatActivity() {
    //ViewModel实例，主要用于数据操作
    abstract var mViewModel:VM

    //使用DataBinding时，对应的Binding实例
    lateinit var mBinding:V


}