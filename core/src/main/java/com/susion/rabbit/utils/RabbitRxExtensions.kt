package com.susion.rabbit.utils

import android.os.Looper
import android.widget.Toast
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-09-24
 */

internal fun runOnIoThread(runnable: () -> Unit, finishCallback: () -> Unit = {}): Disposable {
    return Observable.create<Unit> {
        try {
            it.onNext(runnable())
        } catch (e: Exception) {
            it.onError(e)
        }
        it.onComplete()
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            finishCallback()
        }, {
            finishCallback()
        })
}

internal fun <T> runOnIoThreadWithData(
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
        })
}

fun rabbitTimeFormat(time: Long?): String {
    if (time == null) return ""
    return SimpleDateFormat("MM/dd HH:mm:ss:SSS").format(Date(time))
}

fun rabbitSimpleTimeFormat(time: Long?): String {
    if (time == null) return ""
    return SimpleDateFormat("HH:mm:ss").format(Date(time))
}

fun toastInThread(msg: String) {
    object : Thread("DevTools_Toast_Thread") {
        override fun run() {
            Looper.prepare()
            try {
                Toast.makeText(Rabbit.application, msg, Toast.LENGTH_SHORT).show()
            } catch (e: OutOfMemoryError) {
            }
            Looper.loop()
        }
    }.start()
}