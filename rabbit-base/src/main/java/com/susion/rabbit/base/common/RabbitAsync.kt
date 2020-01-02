package com.susion.rabbit.base.common

import com.susion.rabbit.base.RabbitLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

/**
 * susionwang at 2019-12-17
 */
object RabbitAsync {

    private val TAG = javaClass.simpleName

    fun asyncRun(
        runnable: () -> Unit,
        executor: Executor,
        finishCallback: () -> Unit = {}
    ): Disposable {
        return Observable.create<Unit> {
            try {
                it.onNext(runnable())
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.from(executor)).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                finishCallback()
            }, {
                finishCallback()
                RabbitLog.d(TAG, "asyncRun error")
            })
    }

    fun <T> asyncRunWithResult(
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
                RabbitLog.d(TAG, "asyncRun error")
            })
    }

    fun <T> asyncRunWithResult(
        runnable: () -> T,
        executor: Executor,
        completeCallBack: (result: T) -> Unit
    ): Disposable {
        return Observable.create<T> {
            try {
                it.onNext(runnable())
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.from(executor))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                completeCallBack(it)
            }, {
                RabbitLog.d(TAG, "asyncRun error")
            })
    }


    fun asyncRun(
        runnable: () -> Unit,
        finishCallback: () -> Unit = {}
    ): Disposable {
        return Observable.create<Unit> {
            try {
                it.onNext(runnable())
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                finishCallback()
            }, {
                finishCallback()
                RabbitLog.d(TAG, "asyncRun  error")
            })
    }

}