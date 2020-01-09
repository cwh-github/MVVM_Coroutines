package com.cwh.mvvm_coroutines.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cwh.mvvm_coroutines.R

/**
 * Description:
 * Date：2020/1/9 0009-20:03
 * Author: cwh
 */
class GridFragment:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout,container,false)
    }
}