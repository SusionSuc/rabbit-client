package com.susion.rabbit.tracer.entities;

import com.susion.rabbit.base.entities.RabbitGreenDaoInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2019-10-29
 */

@Entity
public class RabbitBlockFrameInfo implements RabbitGreenDaoInfo {

    @Id(autoincrement = true)
    public Long id;

    public String blockFrameStrackTraceStrList;

    public String blockIdentifier;


    public Long costTime;

    public Long time;

    @Generated(hash = 1110232849)
    public RabbitBlockFrameInfo(Long id, String blockFrameStrackTraceStrList,
            String blockIdentifier, Long costTime, Long time) {
        this.id = id;
        this.blockFrameStrackTraceStrList = blockFrameStrackTraceStrList;
        this.blockIdentifier = blockIdentifier;
        this.costTime = costTime;
        this.time = time;
    }

    @Generated(hash = 2109034768)
    public RabbitBlockFrameInfo() {
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

    public String getBlockFrameStrackTraceStrList() {
        return this.blockFrameStrackTraceStrList;
    }

    public void setBlockFrameStrackTraceStrList(
            String blockFrameStrackTraceStrList) {
        this.blockFrameStrackTraceStrList = blockFrameStrackTraceStrList;
    }

    public String getBlockIdentifier() {
        return this.blockIdentifier;
    }

    public void setBlockIdentifier(String blockIdentifier) {
        this.blockIdentifier = blockIdentifier;
    }

    public Long getCostTime() {
        return this.costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
