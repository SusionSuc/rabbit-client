package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * susionwang at 2019-10-29
 */

@Entity
public class RabbitBlockFrameInfo implements RabbitInfoProtocol{

    @Id(autoincrement = true)
    public Long id;

    public String blockFrameStrackTraceStrList;

    public String blockIdentifier;

    public Long costTime;

    public Long time;

    public String pageName;


    @Generated(hash = 693877624)
    public RabbitBlockFrameInfo(Long id, String blockFrameStrackTraceStrList,
            String blockIdentifier, Long costTime, Long time, String pageName) {
        this.id = id;
        this.blockFrameStrackTraceStrList = blockFrameStrackTraceStrList;
        this.blockIdentifier = blockIdentifier;
        this.costTime = costTime;
        this.time = time;
        this.pageName = pageName;
    }

    @Generated(hash = 2109034768)
    public RabbitBlockFrameInfo() {
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

    @Override
    public String getPageName() {
        return pageName;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

}
