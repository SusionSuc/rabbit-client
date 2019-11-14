package com.susion.rabbit.tracer

/**
 * susionwang at 2019-11-14
 */

object RabbitTracerEventNotifier {

    @JvmField
    var eventNotifier: TracerEvent = FakeEventListener()

    interface TracerEvent {
        fun applicationCreateCostTime(time:Long){}
    }

    class FakeEventListener:TracerEvent

}