package com.susion.rabbit.storage

import android.app.Application
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.config.RabbitStorageConfig
import com.susion.rabbit.base.entities.*
import io.reactivex.disposables.Disposable
import org.greenrobot.greendao.Property
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-10-12
 * 数据库存储操作类
 */
object RabbitStorage {

    var mConfig = RabbitStorageConfig()
    private var eventListeners = ArrayList<EventListener>()
    private lateinit var mApp: Application
    private val disposableList = ArrayList<Disposable>()
    private val DB_THREAD = ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, LinkedBlockingDeque(),
        ThreadFactory { r ->
            Thread(r, "rabbit_db_manager_thread${System.currentTimeMillis()}")
        }).apply {
        allowCoreThreadTimeOut(true)
    }

    private val greenDaoDbManage by lazy {
        RabbitGreenDaoDbManage(mApp, mConfig.greenDaoProvider)
    }

    fun init(application: Application, config: RabbitStorageConfig) {
        mApp = application
        mConfig = config

        //one session 存在的数据
        RabbitAsync.asyncRun({
            mConfig.storageInOneSessionData.forEach {
                greenDaoDbManage.clear(RabbitUtils.nameToInfoClass(it))
            }
        }, DB_THREAD)

        //超出最大限制的数据
        for (info in mConfig.dataMaxSaveCountLimit.entries) {
            val jclass = RabbitUtils.nameToInfoClass(info.key)
            val currentCount = greenDaoDbManage.count(jclass)
            if (currentCount > info.value) {
                greenDaoDbManage.clear(jclass)
            }
        }
    }

    fun <T : RabbitInfoProtocol> getAll(
        ktClass: Class<T>,
        condition: Pair<Property, Any>? = null,
        sortField: String = RabbitInfoProtocol.PROPERTITY_TIME,
        count: Int = 0,
        orderDesc: Boolean = false,
        loadResult: (exceptionList: List<T>) -> Unit
    ) {
        RabbitAsync.asyncRunWithResult({
            greenDaoDbManage.getDatas(
                ktClass,
                condition?.first?.eq(condition.second),
                sortField,
                count,
                orderDesc
            )
        }, DB_THREAD, {
            loadResult(it)
        })
    }

    fun <T : RabbitInfoProtocol> getAllSync(
        ktClass: Class<T>,
        condition: Pair<Property, String>? = null,
        sortField: String = RabbitInfoProtocol.PROPERTITY_TIME,
        count: Int = 0,
        orderDesc: Boolean = false
    ): List<T> {
        return greenDaoDbManage.getDatas(
            ktClass,
            condition?.first?.eq(condition.second),
            sortField,
            count,
            orderDesc
        )
    }

    fun save(obj: RabbitInfoProtocol) {
        val dis = RabbitAsync.asyncRun({
            greenDaoDbManage.save(obj)
        }, DB_THREAD, {
            notifyEventListenerNewDataSave(obj)
        })
        disposableList.add(dis)
    }

    fun saveSync(obj: RabbitInfoProtocol) {
        greenDaoDbManage.save(obj)
        notifyEventListenerNewDataSave(obj)
    }

    fun <T : RabbitInfoProtocol> querySync(clazz: Class<T>, id: Long): T? {
        return greenDaoDbManage.query(clazz, id)
    }

    fun <T : Any> clear(clazz: Class<T>) {
        RabbitAsync.asyncRun({ greenDaoDbManage.clear(clazz) }, DB_THREAD)
    }

    fun <T : Any> delete(clazz: Class<T>, id: Long) {
        RabbitAsync.asyncRun({
            greenDaoDbManage.delete(clazz, id)
        }, DB_THREAD)
    }

    fun <T : RabbitInfoProtocol> delete(clazz: Class<T>, condition: Pair<Property, String>) {
        RabbitAsync.asyncRun({
            greenDaoDbManage.delete(clazz, condition.first.eq(condition.second))
        }, DB_THREAD)
    }

    fun <T : Any> totalCount(clazz: Class<T>): Long {
        return greenDaoDbManage.count(clazz)
    }

    fun <T : RabbitInfoProtocol> distinct(
        clazz: Class<T>,
        columnName: String,
        loadResult: (result: List<String>) -> Unit
    ) {
        RabbitAsync.asyncRunWithResult({
            greenDaoDbManage.distinct(clazz, columnName)
        }, {
            loadResult(it)
        })
    }

    fun <T : RabbitInfoProtocol> updateOrCreate(
        clazz: Class<T>,
        obj: RabbitInfoProtocol,
        id: Long
    ) {
        RabbitAsync.asyncRun({
            greenDaoDbManage.update(clazz, obj, id)
        }, DB_THREAD)
    }

    interface EventListener {
        fun onStorageData(obj: Any)
    }

    fun clearDataByMonitorName(monitorName: String) {
        when (monitorName) {
            RabbitMonitorProtocol.APP_SPEED.name -> {
                clear(RabbitAppStartSpeedInfo::class.java)
                clear(RabbitPageSpeedInfo::class.java)
            }

            RabbitMonitorProtocol.EXCEPTION.name -> {
                clear(RabbitExceptionInfo::class.java)
            }

            RabbitMonitorProtocol.MEMORY.name -> {
                clear(RabbitMemoryInfo::class.java)
            }

            RabbitMonitorProtocol.SLOW_METHOD.name -> {
                clear(RabbitSlowMethodInfo::class.java)
            }

            RabbitMonitorProtocol.NET.name -> {
                clear(RabbitHttpLogInfo::class.java)
            }

            RabbitMonitorProtocol.BLOCK_CALL.name -> {
                clear(RabbitIoCallInfo::class.java)
            }

            RabbitMonitorProtocol.GLOBAL_MONITOR.name -> {
                clear(RabbitAppPerformanceInfo::class.java)
            }

            RabbitMonitorProtocol.BLOCK.name -> {
                clear(RabbitBlockFrameInfo::class.java)
            }
        }
    }

    fun clearAllData() {
        clearDataByMonitorName(RabbitMonitorProtocol.APP_SPEED.name)
        clearDataByMonitorName(RabbitMonitorProtocol.EXCEPTION.name)
        clearDataByMonitorName(RabbitMonitorProtocol.MEMORY.name)
        clearDataByMonitorName(RabbitMonitorProtocol.SLOW_METHOD.name)
        clearDataByMonitorName(RabbitMonitorProtocol.NET.name)
        clearDataByMonitorName(RabbitMonitorProtocol.BLOCK_CALL.name)
        clearDataByMonitorName(RabbitMonitorProtocol.GLOBAL_MONITOR.name)
        clearDataByMonitorName(RabbitMonitorProtocol.BLOCK.name)
    }

    private fun notifyEventListenerNewDataSave(obj: Any) {
        eventListeners.forEach {
            it.onStorageData(obj)
        }
    }

    fun addEventListener(eventListener: EventListener) {
        eventListeners.add(eventListener)
    }

    fun removeEventListener(eventListener: EventListener) {
        eventListeners.remove(eventListener)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}