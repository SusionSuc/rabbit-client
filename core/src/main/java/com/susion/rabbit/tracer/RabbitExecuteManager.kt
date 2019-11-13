package com.susion.rabbit.tracer

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 * susionwang at 2019-10-30
 */
object RabbitExecuteManager {

    val DB_THREAD = Executors.newFixedThreadPool(1, object : ThreadFactory {
        override fun newThread(r: Runnable): Thread {
            return Thread(r, "rabbit_trace_frame_notice_thread")
        }
    })

}