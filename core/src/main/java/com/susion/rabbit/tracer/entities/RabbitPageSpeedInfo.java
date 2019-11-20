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

    public long drawFinishTime;

    public long resumeEndTime;

    @Keep
    public long getPageCreateTime(){
        return  createEndTime - createStartTime;
    }

    @Keep
    public long getPageRenderTime(){
        return  drawFinishTime - createStartTime;
    }

    @Generated(hash = 600818591)
    public RabbitPageSpeedInfo(Long id, String pageName, long time, long createStartTime, long createEndTime,
            long drawFinishTime, long resumeEndTime) {
        this.id = id;
        this.pageName = pageName;
        this.time = time;
        this.createStartTime = createStartTime;
        this.createEndTime = createEndTime;
        this.drawFinishTime = drawFinishTime;
        this.resumeEndTime = resumeEndTime;
    }

    @Generated(hash = 1224997673)
    public RabbitPageSpeedInfo() {
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

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getCreateStartTime() {
        return this.createStartTime;
    }

    public void setCreateStartTime(Long createStartTime) {
        this.createStartTime = createStartTime;
    }

    public Long getCreateEndTime() {
        return this.createEndTime;
    }

    public void setCreateEndTime(Long createEndTime) {
        this.createEndTime = createEndTime;
    }

    public Long getDrawFinishTime() {
        return this.drawFinishTime;
    }

    public void setDrawFinishTime(Long drawFinishTime) {
        this.drawFinishTime = drawFinishTime;
    }

    public Long getResumeEndTime() {
        return this.resumeEndTime;
    }

    public void setResumeEndTime(Long resumeEndTime) {
        this.resumeEndTime = resumeEndTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setCreateStartTime(long createStartTime) {
        this.createStartTime = createStartTime;
    }

    public void setCreateEndTime(long createEndTime) {
        this.createEndTime = createEndTime;
    }

    public void setDrawFinishTime(long drawFinishTime) {
        this.drawFinishTime = drawFinishTime;
    }

    public void setResumeEndTime(long resumeEndTime) {
        this.resumeEndTime = resumeEndTime;
    }


}
