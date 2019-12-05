package com.susion.rabbit.report.entities;

import com.susion.rabbit.base.entities.RabbitGreenDaoInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

/**
 * susionwang at 2019-12-05
 */
@Entity
public class RabbitReportInfo implements RabbitGreenDaoInfo {

    @Id(autoincrement = true)
    public Long id;

    public String infoStr;

    public Long time;

    public String pageName;

    public String deviceInfoStr;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RabbitReportInfo that = (RabbitReportInfo) o;
        return Objects.equals(infoStr, that.infoStr) &&
                Objects.equals(time, that.time) &&
                Objects.equals(pageName, that.pageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infoStr, time, pageName);
    }

    public RabbitReportInfo(String infoStr, Long time, String pageName) {
        this.infoStr = infoStr;
        this.time = time;
        this.pageName = pageName;
    }

    public RabbitReportInfo(String infoStr, Long time, String pageName, String deviceInfoStr) {
        this.infoStr = infoStr;
        this.time = time;
        this.pageName = pageName;
        this.deviceInfoStr = deviceInfoStr;
    }

    @Generated(hash = 1326223370)
    public RabbitReportInfo(Long id, String infoStr, Long time, String pageName,
                            String deviceInfoStr) {
        this.id = id;
        this.infoStr = infoStr;
        this.time = time;
        this.pageName = pageName;
        this.deviceInfoStr = deviceInfoStr;
    }

    @Generated(hash = 2125059637)
    public RabbitReportInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInfoStr() {
        return this.infoStr;
    }

    public void setInfoStr(String infoStr) {
        this.infoStr = infoStr;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getPageName() {
        return this.pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getDeviceInfoStr() {
        return this.deviceInfoStr;
    }

    public void setDeviceInfoStr(String deviceInfoStr) {
        this.deviceInfoStr = deviceInfoStr;
    }

    @Override
    public String getSortField() {
        return "time";
    }

    @Override
    public long getLongTime() {
        return time;
    }
}
