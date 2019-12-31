package com.cwh.mvvm_coroutines_base.base.view

/**
 * Description:懒加载Fragment 结合setMaxLife()方法一起使用
 * 对于FragmentPagerAdapter 需要设置参数 BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT，
 * 此时会自动设置为Lifecycle.State.STARTED,在结合ViewPager使用时，只有当前显示的Fragment
 * 为onResume状态，其他均为onStart 或者 onPause状态，当Fragment对用户展示时才走到
 * onResume状态
 *
 * Date：2019/12/31 0031-15:00
 * Author: cwh
 */
abstract class BaseLazyFragment : BaseFragment() {
}