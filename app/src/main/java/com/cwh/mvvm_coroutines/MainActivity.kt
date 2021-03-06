package com.cwh.mvvm_coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cwh.mvvm_coroutines.Fragment.GridFragment
import com.cwh.mvvm_coroutines.Fragment.LinearFragment
import com.cwh.mvvm_coroutines.Fragment.StaggeredGridFragment
import kotlinx.android.synthetic.main.activity_main.*

//Base_URL="http://news-at.zhihu.com/api/4/"

class MainActivity : AppCompatActivity() {

    val mFragments= mutableListOf<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        with(mFragments){
            add(LinearFragment())
            add(GridFragment())
            add(StaggeredGridFragment())
        }
        mViewPager.offscreenPageLimit=2
        mViewPager.adapter=MyViewpagerAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT)

    }


    inner class MyViewpagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

    }
}
