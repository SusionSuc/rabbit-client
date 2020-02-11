package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2019-12-16
 */

@Entity
public class RabbitFPSInfo implements RabbitInfoProtocol {

    @Id(autoincrement = true)
    public Long id;

    public String pageName;

    public int maxFps;

    public int minFps;

    public int avgFps;

    public Long time;


    public RabbitFPSInfo() {
    }

    public RabbitFPSInfo(int maxFps, int minFps, int avgFps) {
        this.maxFps = maxFps;
        this.minFps = minFps;
        this.avgFps = avgFps;
    }

    @Generated(hash = 1070518384)
    public RabbitFPSInfo(Long id, String pageName, int maxFps, int minFps,
                         int avgFps, Long time) {
        this.id = id;
        this.pageName = pageName;
        this.maxFps = maxFps;
        this.minFps = minFps;
        this.avgFps = avgFps;
        this.time = time;
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

    public int getMaxFps() {
        return this.maxFps;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public int getMinFps() {
        return this.minFps;
    }

    public void setMinFps(int minFps) {
        this.minFps = minFps;
    }

    public int getAvgFps() {
        return this.avgFps;
    }

    public void setAvgFps(int avgFps) {
        this.avgFps = avgFps;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
