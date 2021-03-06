package com.cwh.mvvm_coroutines.db

import androidx.room.TypeConverter

/**
 * Description:
 * Date：2020/3/3-17:31
 * Author: cwh
 */
class Convert {

    @TypeConverter
    fun arrayToString(data:ArrayList<String>?):String?{
        return if(data.isNullOrEmpty()){
            null
        }else{
            val sb=StringBuffer()
            data!!.forEachIndexed{index,str->
                if(index==0){
                    sb.append(str)
                }else{
                    sb.append("~").append(str)
                }
            }
            sb.toString()
        }
    }

    @TypeConverter
    fun stringToArray(str:String?):ArrayList<String>?{
        return if(str.isNullOrEmpty()){
            null
        }else{
            val array= ArrayList<String>()
            val result=str.split("~")
            result.forEach {
                array.add(it)
            }
            array
        }
    }
}