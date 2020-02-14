package com.susion.rabbit.base.entities;

import android.util.Log;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * susionwang at 2019-11-20
 */
@Entity
public class RabbitAppStartSpeedInfo implements RabbitInfoProtocol{

    @Id(autoincrement = true)
    public Long id;

    public long time;

    public long createStartTime;

    public long createEndTime;

    public long fullShowCostTime;

    @Generated(hash = 1149395827)
    public RabbitAppStartSpeedInfo(Long id, long time, long createStartTime,
                                   long createEndTime, long fullShowCostTime) {
        this.id = id;
        this.time = time;
        this.createStartTime = createStartTime;
        this.createEndTime = createEndTime;
        this.fullShowCostTime = fullShowCostTime;
    }

    @Generated(hash = 1557123775)
    public RabbitAppStartSpeedInfo() {
    }

    public Long appCreateCost() {
        return createEndTime - createStartTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return this.time;
    }

    @Override
    public String getPageName() {
        return "";
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getCreateStartTime() {
        return this.createStartTime;
    }

    public void setCreateStartTime(long createStartTime) {
        this.createStartTime = createStartTime;
    }

    public long getCreateEndTime() {
        return this.createEndTime;
    }

    public void setCreateEndTime(long createEndTime) {
        this.createEndTime = createEndTime;
    }

    public long getFullShowCostTime() {
        return this.fullShowCostTime;
    }

    public void setFullShowCostTime(long fullShowCostTime) {
        this.fullShowCostTime = fullShowCostTime;
    }

}
