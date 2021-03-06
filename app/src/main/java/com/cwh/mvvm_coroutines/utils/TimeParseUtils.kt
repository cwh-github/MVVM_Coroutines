package com.cwh.mvvm_coroutines.utils

import com.cwh.mvvm_coroutines_base.utils.TimeCovertUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Description:
 * Date：2020/3/3-13:07
 * Author: cwh
 */
object TimeParseUtils {


    /**
     * 将当前时间转换为20200303的数据
     */
    fun currentTime2Long():Long{
        val calendar=Calendar.getInstance(Locale.CHINA)
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        return "$year${formatUnit(month)}${formatUnit(day)}".toLong()
    }


    /**
     * 获取为几月几日
     * 如：20200303  3月2日
     */
    fun mothAndDay(date: Long):String{
        if(date<100){
            return date.toString()
        }
        val dateStr=date.toString()
        val month=dateStr.substring(4,6).toInt()
        val day=dateStr.substring(6).toInt()
        return "${month}月${day}日"

    }

    /**
     * 获取给定时间的前n天
     * 如 20200303 的前一天 20200302 前3天 20200229
     * @param n 指定为前n天前的时间
     */
    fun beforeTime2Long(date:Long,n:Int=1):Long{
        val dateStr=date.toString()
        val year=dateStr.substring(0,4)
        val month=dateStr.substring(4,6)
        val day=dateStr.substring(6)
        val formatDateStr="$year-$month-$day"
        val simpleDate= SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val formatDate=simpleDate.parse(formatDateStr)
        val calendar=Calendar.getInstance()
        calendar.time=formatDate
        calendar.add(Calendar.DATE,-n)
        val lastYear=calendar.get(Calendar.YEAR)
        val lastMonth=calendar.get(Calendar.MONTH)+1
        val lastDay=calendar.get(Calendar.DAY_OF_MONTH)
        return "$lastYear${formatUnit(lastMonth)}${formatUnit(lastDay)}".toLong()
    }


    /**
     * 将数据转化为固定格式
     * ru: 9 转为09
     */
    private fun formatUnit(value:Int):String{
        return if(value<10){
            "0$value"
        }else{
            "$value"
        }
    }

    /**
     * 将秒转换成月日时分
     * 如：今天 13:45  03-06 14:23
     *
     *
     */
    fun formatTime(time:Long):String{
        //yyyy-mm-dd HH:MM
        val dateString=TimeCovertUtils.convertToDate(time*1000)
        val hourAndMin=dateString.substring(11)
        val calendar=Calendar.getInstance()
        val simpleDate= SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        calendar.time=simpleDate.parse(simpleDate.format(time*1000))
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val currentYear=TimeCovertUtils.covertYear()
        val currentMonth=TimeCovertUtils.covertMonth()
        val currentDay=TimeCovertUtils.covertDay()
        if(year==currentYear && month==currentMonth && day==currentDay){
            return "今天 $hourAndMin"
        }
        return "${dateString.substring(5,10)} $hourAndMin"
    }

}