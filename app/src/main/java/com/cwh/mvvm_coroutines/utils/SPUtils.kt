package com.cwh.mvvm_coroutines.utils

import android.content.Context
import androidx.core.content.edit
import com.cwh.mvvm_coroutines_base.utils.ConUtils

/**
 * Description:
 * Date：2020/3/7-22:15
 * Author: cwh
 */
object SPUtils {

    const val LAST_REFRESH_TIME="last_refresh_time"
    const val LAST_CLEAN_TIME="last_clean_time"
    const val SP_NAME="config"

    fun getLastTime():Long{
        return ConUtils.mContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .getLong(LAST_REFRESH_TIME,0)
    }

    fun saveRefreshTime(){
        ConUtils.mContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .edit {
                putLong(LAST_REFRESH_TIME,System.currentTimeMillis())
            }
    }

    fun getLastCleanTime()= run {
        ConUtils.mContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .getLong(LAST_CLEAN_TIME,0)
    }

    fun saveCleanTime(){
        ConUtils.mContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .edit {
                putLong(LAST_CLEAN_TIME,System.currentTimeMillis())
            }
    }


}