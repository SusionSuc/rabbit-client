package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2020-01-14
 */
@Entity
public class RabbitGlobalMonitorInfo {

    @Id(autoincrement = true)
    public Long id;

    public Long time;

    public String fpsIds;

    public String memoryIds;

    public String appStardId;

    public String pageSpeedIds;

    public String blockIds;

    public String slowMethodIds;

    @Generated(hash = 630406623)
    public RabbitGlobalMonitorInfo(Long id, Long time, String fpsIds,
            String memoryIds, String appStardId, String pageSpeedIds,
            String blockIds, String slowMethodIds) {
        this.id = id;
        this.time = time;
        this.fpsIds = fpsIds;
        this.memoryIds = memoryIds;
        this.appStardId = appStardId;
        this.pageSpeedIds = pageSpeedIds;
        this.blockIds = blockIds;
        this.slowMethodIds = slowMethodIds;
    }

    @Generated(hash = 1291211931)
    public RabbitGlobalMonitorInfo() {
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

    public void setTime(Long time) {
        this.time = time;
    }

    public String getFpsIds() {
        return this.fpsIds;
    }

    public void setFpsIds(String fpsIds) {
        this.fpsIds = fpsIds;
    }

    public String getMemoryIds() {
        return this.memoryIds;
    }

    public void setMemoryIds(String memoryIds) {
        this.memoryIds = memoryIds;
    }

    public String getAppStardId() {
        return this.appStardId;
    }

    public void setAppStardId(String appStardId) {
        this.appStardId = appStardId;
    }

    public String getPageSpeedIds() {
        return this.pageSpeedIds;
    }

    public void setPageSpeedIds(String pageSpeedIds) {
        this.pageSpeedIds = pageSpeedIds;
    }

    public String getBlockIds() {
        return this.blockIds;
    }

    public void setBlockIds(String blockIds) {
        this.blockIds = blockIds;
    }

    public String getSlowMethodIds() {
        return this.slowMethodIds;
    }

    public void setSlowMethodIds(String slowMethodIds) {
        this.slowMethodIds = slowMethodIds;
    }

}
