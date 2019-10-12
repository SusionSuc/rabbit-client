package com.susion.devtools.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.Toast
import com.susion.devtools.DevTools
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-09-24
 */

internal fun <T> Context.simpleStartActivity(ktClass: Class<T>) where T : Activity {
    startActivity(Intent(this, ktClass))
}

internal fun runOnIoThread(runnable: () -> Unit): Disposable {
    return Observable.create<Unit> {
        try {
            it.onNext(runnable())
        } catch (e: Exception) {
            it.onError(e)
        }
        it.onComplete()
    }.subscribeOn(Schedulers.io()).subscribe()
}

internal fun <T> runOnIoThread(
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

fun devToolsTimeFormat(time: Long): String {
    return SimpleDateFormat("MM/dd HH:mm:ss").format(Date(time))
}

fun toastInThread(msg: String) {
    object : Thread("CrashHandler-uncaughtException-Thread") {
        override fun run() {
            Looper.prepare()
            try {
                Toast.makeText(DevTools.application, msg, Toast.LENGTH_SHORT).show()
            } catch (e: OutOfMemoryError) {
            }
            Looper.loop()
        }
    }.start()
}
