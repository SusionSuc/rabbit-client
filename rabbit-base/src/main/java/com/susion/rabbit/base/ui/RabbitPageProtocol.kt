package com.susion.rabbit.base.ui

/**
 * susionwang at 2019-10-21
 */
interface RabbitPageProtocol {

    var eventListener: PageEventListener?

    interface PageEventListener {
        fun onBack()
    }

    fun setEntryParams(params: Any) {

    }

}