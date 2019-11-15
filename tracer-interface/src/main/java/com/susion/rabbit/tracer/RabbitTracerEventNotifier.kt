package com.susion.rabbit.tracer

/**
 * susionwang at 2019-11-14
 */

object RabbitTracerEventNotifier {

    @JvmField
    var eventNotifier: TracerEvent = FakeEventListener()

    interface TracerEvent {
        fun applicationCreateCostTime(time:Long){}
        fun activityDrawFinish(activity:Any, time: Long){}
        fun activityCreateEnd(activity: Any, time: Long){}
        fun activityCreateStart(activity: Any, time: Long){}
        fun activityResumeEnd(activity: Any, time: Long){}
    }

    class FakeEventListener:TracerEvent

}