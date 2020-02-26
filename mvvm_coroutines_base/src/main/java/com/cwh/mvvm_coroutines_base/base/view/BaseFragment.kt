package com.cwh.mvvm_coroutines_base.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.cwh.mvvm_coroutines_base.utils.ToastUtils
import com.cwh.mvvm_coroutines_base.base.observerEvent
import com.cwh.mvvm_coroutines_base.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * Description:
 * Date：2019/12/31 0031-14:57
 * Author: cwh
 */
abstract class BaseFragment<VM : BaseViewModel<*>, V : ViewDataBinding> : Fragment() {

    /**
     * ViewModel实例，主要用于数据操作
     * 当不需要ViewModel是，ViewModel类型为NoViewModel即
     * 可(具有BaseViewModel的功能)
     *
     */
    protected abstract val mViewModel: VM

    /**
     * 布局对应的Id
     */
    protected abstract val layoutId: Int

    /**
     * ViewModel在DataBinding中生成的Id
     * 当使用ViewDataBinding的类型为ViewDataBinding时，mViewModelVariableId=-1即可
     */
    protected abstract val mViewModelVariableId: Int

    //使用DataBinding时，对应的Binding实例
    private lateinit var mBinding: V

    lateinit var mActivity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    /**
     * 是否使用了DataBinding
     * 当不需要使用DataBinding时，ViewDataBinding的类型直接写为ViewDataBinding即可
     */
    private fun isUseViewDataBinding(): Boolean {
        val type = javaClass.genericSuperclass as? ParameterizedType
        return if (type != null) {
            val clz = type!!.actualTypeArguments[1] as Class<*>
            ViewDataBinding::class.java != clz && clz.isAssignableFrom(ViewDataBinding::class.java)
        } else false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (isUseViewDataBinding()) {
            mBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
            mBinding.setVariable(mViewModelVariableId, mViewModel)
            mBinding.lifecycleOwner = this.viewLifecycleOwner
            mBinding.root
        } else inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(mViewModel)
        registerDefUIEvent()
        initDataAndView()
        initViewObserver()
    }


    /**
     * 初始化一些Data  and View
     */
    protected open fun initDataAndView() {

    }

    /**
     * 注册一些在LiveData数据变化时，需要改变UI的Observer
     */
    protected open fun initViewObserver() {

    }


    /**
     * 观察BaseViewModel中常用的一些事件
     */
    protected open fun registerDefUIEvent() {

        with(mViewModel.mDefUIEvent) {

            mToastEvent.observerEvent(this@BaseFragment) { msg ->
                activity?.let {
                    ToastUtils.showToast(it.applicationContext, msg)
                }
            }

            onBackEvent.observerEvent(this@BaseFragment) {
                activity?.let {
                    it.onBackPressed()
                }
            }

            onFinishEvent.observerEvent(this@BaseFragment) {
                activity?.let {
                    it.finish()
                }
            }

            mStartActivity.observerEvent(this@BaseFragment) { cls ->
                activity?.let {
                    startActivity(Intent(it, cls))
                }

            }

        }

    }

    /**
     * 创建ViewModel，由于现在定义的ViewModel含有Repository，
     * 所以一般能调用该方法生成ViewModel，需要自己创建Factory来创建
     *
     * 此方法调用的是系统默认的{@link AndroidViewModelFactory}
     *
     * 此Factory默认的是找一个以application为参数的构造方法构造实例，如果此构造方法不存在，会报错，
     * 就算生成成功，此时Repository为null,不能调用Repository的方法获取数据
     *
     * @param cls
     * @param <T>
     * @return
    </T> */
    fun <T : ViewModel> createViewModel(fragment: Fragment, cls: Class<T>): T {
        return ViewModelProviders.of(fragment).get(cls)
    }

    /**
     * 创建ViewModel，由于现在定义的ViewModel含有Repository，
     * 所以一般能调用该方法生成ViewModel，需要自己创建Factory来创建
     *
     * 此方法调用的是系统默认的{@link AndroidViewModelFactory}
     *
     * 此Factory默认的是找一个以application为参数的构造方法构造实例，如果此构造方法不存在，会报错，
     * 就算生成成功，此时Repository为null,不能调用Repository的方法获取数据
     *
     * @param cls
     * @param <T>
     * @return</T>
     * */
    open fun <T : ViewModel> createViewModel(activity: FragmentActivity, cls: Class<T>): T {
        return ViewModelProviders.of(activity).get(cls)
    }

    private val REQUEST_PERMISSON_CODE=1001

    /**
     * check Permission
     *
     *  如果需要申请权限，进行权限申请的请求
     */
    open fun checkPermission(permissions:Array<String>):Boolean{
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
            return true
        }

        var result=true
        val needRequestPermissions= mutableListOf<String>()
        permissions.forEach {
            if(ContextCompat.checkSelfPermission(mActivity,it)== PackageManager.PERMISSION_DENIED){
                result=false
                needRequestPermissions.add(it)
            }
        }
        if(needRequestPermissions.size>0){
            requestPermissions(needRequestPermissions.toTypedArray(),REQUEST_PERMISSON_CODE)
        }
        return result
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_PERMISSON_CODE->{
                val mNeedRequestPermissions= mutableListOf<String>()
                grantResults.forEachIndexed { index, result ->
                    if(result== PackageManager.PERMISSION_DENIED){
                        mNeedRequestPermissions.add(permissions[index])
                    }
                }
                if(mNeedRequestPermissions.size>0){
                    //是否需要展示为什么需要请求权限的对话框
                    var resultRational = true
                    mNeedRequestPermissions.forEach {
                        if(!shouldShowRequestPermissionRationale(it)){
                            resultRational=false
                            return@forEach
                        }
                    }
                    //展示为什么需要请求权限的对话框
                    if(resultRational){
                        showRationaleDialog(mNeedRequestPermissions)
                        //打开设置界面，手动开启权限
                    }else{
                        showOpenSettingDialog(mNeedRequestPermissions)
                    }

                }else{
                    onPermissionGrant()
                }

            }
        }

    }

    /**
     * 需要解释为什么需要权限是的操作
     * 显示对话框
     */
    protected open fun showRationaleDialog(mNeedReqPermissions: MutableList<String>) {

    }

    /**
     * 显示对话框，跳转设置界面开启权限
     */
    protected open fun showOpenSettingDialog(mNeedReqPermissions: MutableList<String>) {

    }

    /**
     * 当请求权限全部通过时，执行该方法
     */
    protected open fun onPermissionGrant() {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (::mBinding.isInitialized) {
            mBinding.unbind()
        }
    }


}
