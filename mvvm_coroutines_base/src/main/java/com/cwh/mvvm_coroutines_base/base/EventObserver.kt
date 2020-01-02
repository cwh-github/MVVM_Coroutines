package com.cwh.mvvm_coroutines_base.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Description:
 * Date：2020/1/2 0002-14:25
 * Author: cwh
 */


class EventObserver<T>( private var onEventUnHandleContent:(T)->Unit):Observer<Event<T>>{
    override fun onChanged(t: Event<T>?) {
        t?.getContentIfNotHandled()?.let {
            onEventUnHandleContent(it)
        }
    }
}


inline fun <T> LiveData<Event<T>>.observerEvent(owner: LifecycleOwner,
                                         crossinline onEventUnHandleContent:(T)->Unit ){
    observe(owner, Observer {event->
        event.getContentIfNotHandled()?.let {
           onEventUnHandleContent(it)
       }
    })
}