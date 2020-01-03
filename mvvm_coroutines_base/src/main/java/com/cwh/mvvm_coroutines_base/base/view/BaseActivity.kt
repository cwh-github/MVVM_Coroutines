package com.cwh.mvvm_coroutines_base.base.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.cwh.mvvm_coroutines_base.Utils.ToastUtils
import com.cwh.mvvm_coroutines_base.base.observerEvent
import com.cwh.mvvm_coroutines_base.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * Description:
 * Date：2019/12/31 0031-14:58
 * Author: cwh
 */
abstract class BaseActivity<VM : BaseViewModel<*>, V : ViewDataBinding> : AppCompatActivity() {
    /**
     *  ViewModel实例，主要用于数据操作
     *  当不需要ViewModel时，直接写明
     *  类型为NoViewModel即可
     */
    protected abstract val mViewModel: VM

    /**
     * 布局文件对应的Id
     */
    protected abstract val layoutId: Int

    /**
     * ViewModel在DataBinding中生成的Id
     * 当使用ViewDataBinding时，mViewModelVariableId=-1即可
     */
    protected abstract val mViewModelVariableId: Int

    //使用DataBinding时，对应的Binding实例
    private lateinit var mBinding: V




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParams()
        initDataBinding()
        registerDefUIEvent()
        initDataAndView()
        initViewObserver()
    }


    /**
     * 需要在setContentView前面初始化得一些参数
     * 例如：设置Activity竖屏等
     */
    protected open fun initParams() {

    }

    /**
     * 初始化一些数据和View
     */
    protected open fun initDataAndView() {

    }

    /**
     * 注册需要根据LiveData中数据变化，而改变UI的Observer
     */
    protected open fun initViewObserver() {

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

    /**
     * 初始化DataBinding
     */
    private fun initDataBinding() {
        if (isUseViewDataBinding()) {
            mBinding = DataBindingUtil.setContentView(this, layoutId)
            //将ViewModel与DataBinding中的生成的Id关联
            mBinding.setVariable(mViewModelVariableId, mViewModel)
            mBinding.lifecycleOwner = this
            //ViewModel观察生命周期
        } else {
            setContentView(layoutId)
        }
        lifecycle.addObserver(mViewModel)
    }

    /**
     * 观察BaseViewModel中常用的一些事件
     */
    protected open fun registerDefUIEvent() {

        with(mViewModel.mDefUIEvent) {

            mToastEvent.observerEvent(this@BaseActivity) {
                ToastUtils.showToast(this@BaseActivity.applicationContext, it)
            }

            onBackEvent.observerEvent(this@BaseActivity) {
                onBackPressed()
            }

            onFinishEvent.observerEvent(this@BaseActivity) {
                finish()
            }

            mStartActivity.observerEvent(this@BaseActivity) {
                startActivity(Intent(this@BaseActivity, it))
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
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true
        }

        var result=true
        val needRequestPermissions= mutableListOf<String>()
        permissions.forEach {
            if(ContextCompat.checkSelfPermission(this,it)==PackageManager.PERMISSION_DENIED){
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
                    if(result==PackageManager.PERMISSION_DENIED){
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


    override fun onDestroy() {
        super.onDestroy()
        if (::mBinding.isInitialized) {
            mBinding.unbind()
        }
    }


}