package com.cwh.mvvm_coroutines_base.base.repository

/**
 * Description:包含本地数据和远程数据的
 * 数据仓库
 * Date：2019/12/31 0031-15:58
 * Author: cwh
 */
abstract class BaseRepository<R : BaseRemoteRepository, L : BaseLocalRepository>(
    val remote: R,
    val local: L
) : IRepository {

    override fun onClear() {
        remote.onClear()
        local.onClear()
    }
}