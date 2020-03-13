package com.susion.rabbit.demo.test

/**
 * susionwang at 2020-03-11
 * 获取堆栈耗时测试，
 *
 *  Throwable().stackTrace的性能优于直接拿线程的堆栈
 */
fun main(){

    val count = 10000

    var startTime = System.currentTimeMillis()
    (0..count).forEach {
        Thread.currentThread().stackTrace
    }
    System.out.println("cost time -> ${System.currentTimeMillis() - startTime}")

    startTime = System.currentTimeMillis()
    (0..count).forEach {
        Throwable().stackTrace
    }
    System.out.println("cost time -> ${System.currentTimeMillis() - startTime}")



}
