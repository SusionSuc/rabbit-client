package com.susion.rabbit.storage

import com.susion.rabbit.RabbitLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.greendao.Property
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-10-12
 * 内容存储的基类
 * 封装数据层, 数据库异步操作
 */
object RabbitDbStorageManager {

    private val TAG = javaClass.simpleName

    private val disposableList = ArrayList<Disposable>()
    private val DB_THREAD = ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, LinkedBlockingDeque(),
        ThreadFactory { r ->
            Thread(r, "rabbit_db_manager_thread${System.currentTimeMillis()}")
        }).apply {
        allowCoreThreadTimeOut(true)
    }
    private val greenDaoDbManage by lazy {
        RabbitGreenDaoDbManage(RabbitStorage.application!!)
    }

    /**
     * 查询数据集合
     *
     * 默认按 time 字段 降序排序。 FIX: 有点hardcode
     * */
    fun <T : Any> getAll(
        ktClass: Class<T>,
        condition: Pair<Property, String>? = null,
        sortField: String = "time",
        count: Int = 0,
        orderDesc: Boolean = true,
        loadResult: (exceptionList: List<T>) -> Unit
    ) {
        runOnDbThreadWithResultData({
            greenDaoDbManage.getDatasWithDescendingSort(
                ktClass,
                condition?.first?.eq(condition.second),
                sortField,
                count,
                orderDesc
            )
        }, {
            loadResult(it)
        })
    }

    fun save(obj: Any) {
        val dis = runOnDbThread({
            greenDaoDbManage.saveObj(obj)
        })
        disposableList.add(dis)
        RabbitStorage.mConfig.eventListener?.onStorageData(obj)
    }

    fun saveSync(obj: Any ) {
        greenDaoDbManage.saveObj(obj)
        RabbitStorage.mConfig.eventListener?.onStorageData(obj)
    }

    fun <T : Any> clearAllData(clazz: Class<T>) {
        runOnDbThread({ greenDaoDbManage.clearAllData(clazz) })
    }

    fun <T : Any> delete(clazz: Class<T>, id: Long) {
        runOnDbThread({
            greenDaoDbManage.deleteById(clazz, id)
        })
    }

    fun <T : Any> dataCount(clazz: Class<T>): Long {
        return greenDaoDbManage.allDataCount(clazz)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

    fun clearOldSessionData() {
        runOnDbThread({
            greenDaoDbManage.clearOldSessionData()
        })
    }

    private fun runOnDbThread(runnable: () -> Unit, finishCallback: () -> Unit = {}): Disposable {
        return Observable.create<Unit> {
            try {
                it.onNext(runnable())
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.from(DB_THREAD)).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                finishCallback()
            }, {
                finishCallback()
                RabbitLog.d(TAG, "runOnDbThread load error")
            })
    }

    /**
     * 从数据库获取数据，然后在主线程回调
     * */
    private fun <T> runOnDbThreadWithResultData(
        runnable: () -> T,
        completeCallBack: (result: T) -> Unit
    ): Disposable {
        return Observable.create<T> {
            try {
                it.onNext(runnable())
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                completeCallBack(it)
            }, {
                RabbitLog.d(TAG, "runOnDbThreadWithResultData load error")
            })
    }

}