package com.susion.rabbit.ui.entities

import com.susion.rabbit.base.entities.RabbitIoCallInfo

/**
 * create by susionwang at 2020-01-12
 */
class RabbitBlockCallList(val list:List<RabbitUiSimpleCallInfo>)

class RabbitUiSimpleCallInfo(val invokeStr:String, val becalledStr:String)