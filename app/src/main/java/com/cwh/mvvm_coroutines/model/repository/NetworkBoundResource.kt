//package com.cwh.mvvm_coroutines.model.repository
//
//import androidx.annotation.MainThread
//import androidx.annotation.WorkerThread
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MediatorLiveData
//import com.cwh.mvvm_coroutines_base.base.Result
//import java.lang.Exception
//
///**
// * Description:
// * https://developer.android.google.cn/jetpack/docs/guide
// *
// *  主要功能用于协调获取数据的源，使其 数据单一可信来源（https://developer.android.google.cn/jetpack/docs/guide#truth）
// *  一般有数据库时，本地数据库作为单一可信数据来源
// *  在请求到网络数据后，将数据存储进数据库，通过liveData,在数据库发生改变时，将
// *  最新数据重数据库返回，再显示在UI上
// * Date：2020/3/4-17:03
// * Author: cwh
// *
// * @param RequestType  从接口获取来的数据类型
// *
// * @param ResultType   需要显示在UI上的数据类型
// */
//
//// ResultType: Type for the Resource data. 需要显示在UI上的数据类型
//// RequestType: Type for the API response. 从接口获取来的数据类型
//
//abstract class NetworkBoundResource<ResultType, RequestType> {
//
//
//    private val result=MediatorLiveData<Result<ResultType>>()
//
//    suspend fun initData(){
//        //初始状态为加载中
//        result.value= Result.loading(null)
//        val dbResult=loadFromDb()
//        result.addSource(dbResult){ resultType ->
//            result.removeSource(dbResult)
//            if(shouldRequestFromServer(resultType)){
//                fetchFromNetWork(dbResult)
//            }else{
//                result.addSource(dbResult){
//                    setValue(Result.success(it))
//                }
//            }
//        }
//
//    }
//
//    private inline fun fetchFromNetWork(dbResult: LiveData<ResultType>) {
//        try {
//            val netResult=loadFromServer()
//            result.removeSource(dbResult)
//
//            //在loading状态时，先将数据库的值给UI显示，(我这里的逻辑并不需要)
////            result.addSource(dbResult) { newData ->
////                setValue(Result.loading(newData))
////            }
//
//
//
//
//        }catch (e:Exception){
//            //从网络获取数据失败，显示数据库原来的数据
//            result.addSource(dbResult){
//                setValue(Result.success(it))
//            }
//            throw e
//        }
//
//    }
//
//    @MainThread
//    private fun setValue(newValue: Result<ResultType>) {
//        if (result.value != newValue) {
//            result.value = newValue
//        }
//    }
//
//
//    // Called to save the result of the API response into the database
//    /**
//     * 将从api获取的数据保存到DB
//     */
//    @WorkerThread
//    abstract suspend fun saveDataFromServerToDB(item: RequestType)
//
//    // Called with the data in the database to decide whether to fetch
//    // potentially updated data from the network.
//    /**
//     * 是否需要从Server获取数据
//     */
//    @MainThread
//    abstract fun shouldRequestFromServer(data: ResultType?): Boolean
//
//    // Called to get the cached data from the database.
//    /**
//     * 数据库获取数据
//     */
//    @MainThread
//    abstract suspend fun loadFromDb(): LiveData<ResultType>
//
//    // Called to create the API call.
//    /**
//     * 获取server数据
//     */
//    @MainThread
//    abstract suspend fun loadFromServer(): RequestType
//
//    /**
//     * 将数据转为liveData返回
//     */
//    // Returns a LiveData object that represents the resource that's implemented
//    // in the base class.
//    fun asLiveData(): LiveData<Result<ResultType>> = result
//}
//
//
//
//
