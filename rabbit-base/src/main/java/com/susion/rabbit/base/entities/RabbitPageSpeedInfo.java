package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

/**
 * susionwang at 2019-11-19
 * 页面测速信息
 */
@Entity
public class RabbitPageSpeedInfo  implements RabbitInfoProtocol{

    @Id(autoincrement = true)
    public Long id;

    public String pageName = "";

    public Long time;

    public long createStartTime;

    public long createEndTime;

    public long inflateFinishTime;

    public long fullDrawFinishTime;

    public long resumeEndTime;

    public String apiRequestCostString;

    @Generated(hash = 582827660)
    public RabbitPageSpeedInfo(Long id, String pageName, Long time,
            long createStartTime, long createEndTime, long inflateFinishTime,
            long fullDrawFinishTime, long resumeEndTime,
            String apiRequestCostString) {
        this.id = id;
        this.pageName = pageName;
        this.time = time;
        this.createStartTime = createStartTime;
        this.createEndTime = createEndTime;
        this.inflateFinishTime = inflateFinishTime;
        this.fullDrawFinishTime = fullDrawFinishTime;
        this.resumeEndTime = resumeEndTime;
        this.apiRequestCostString = apiRequestCostString;
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

    public Long getTime() {
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

    public String getApiRequestCostString() {
        return this.apiRequestCostString;
    }

    public void setApiRequestCostString(String apiRequestCostString) {
        this.apiRequestCostString = apiRequestCostString;
    }

    public void setTime(Long time) {
        this.time = time;
    }


}
