package com.susion.rabbit.tracer.entities;

import com.susion.rabbit.base.entities.RabbitGreenDaoInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * susionwang at 2019-11-19
 * 页面测速信息
 */
@Entity
public class RabbitPageSpeedInfo implements RabbitGreenDaoInfo {

    @Id(autoincrement = true)
    public Long id;

    public String pageName = "";

    public long time;

    public long createStartTime;

    public long createEndTime;

    public long inflateFinishTime;

    public long fullDrawFinishTime;

    public long resumeEndTime;

    @Generated(hash = 2079077552)
    public RabbitPageSpeedInfo(Long id, String pageName, long time,
            long createStartTime, long createEndTime, long inflateFinishTime,
            long fullDrawFinishTime, long resumeEndTime) {
        this.id = id;
        this.pageName = pageName;
        this.time = time;
        this.createStartTime = createStartTime;
        this.createEndTime = createEndTime;
        this.inflateFinishTime = inflateFinishTime;
        this.fullDrawFinishTime = fullDrawFinishTime;
        this.resumeEndTime = resumeEndTime;
    }

    @Generated(hash = 1224997673)
    public RabbitPageSpeedInfo() {
    }

    @Keep
    public long getPageCreateTime(){
        return  createEndTime - createStartTime;
    }

    @Keep
    public long getPageInflateTime(){
        return  inflateFinishTime - createStartTime;
    }

    public long getFullRenderTime(){
        return fullDrawFinishTime - createStartTime;
    }

    @Override
    public String getSortField() {
        return "time";
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageName() {
        return this.pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public long getTime() {
        return this.time;
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

    public long getInflateFinishTime() {
        return this.inflateFinishTime;
    }

    public void setInflateFinishTime(long inflateFinishTime) {
        this.inflateFinishTime = inflateFinishTime;
    }

    public long getFullDrawFinishTime() {
        return this.fullDrawFinishTime;
    }

    public void setFullDrawFinishTime(long fullDrawFinishTime) {
        this.fullDrawFinishTime = fullDrawFinishTime;
    }

    public long getResumeEndTime() {
        return this.resumeEndTime;
    }

    public void setResumeEndTime(long resumeEndTime) {
        this.resumeEndTime = resumeEndTime;
    }



}
