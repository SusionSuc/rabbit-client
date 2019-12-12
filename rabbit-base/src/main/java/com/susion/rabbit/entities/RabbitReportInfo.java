package com.susion.rabbit.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2019-12-05
 */
@Entity
public class RabbitReportInfo  {

    @Id(autoincrement = true)
    public Long id;

    public String infoStr;

    public Long time;

    public String pageName;

    public String deviceInfoStr;

    public String type;

    @Generated(hash = 230216145)
    public RabbitReportInfo(Long id, String infoStr, Long time, String pageName,
            String deviceInfoStr, String type) {
        this.id = id;
        this.infoStr = infoStr;
        this.time = time;
        this.pageName = pageName;
        this.deviceInfoStr = deviceInfoStr;
        this.type = type;
    }

    @Generated(hash = 2125059637)
    public RabbitReportInfo() {
    }

    public RabbitReportInfo(String infoStr, Long time, String pageName, String deviceInfoStr) {
        this.infoStr = infoStr;
        this.time = time;
        this.pageName = pageName;
        this.deviceInfoStr = deviceInfoStr;
    }


    public RabbitReportInfo(Long id, String infoStr, Long time, String pageName, String deviceInfoStr) {
        this.id = id;
        this.infoStr = infoStr;
        this.time = time;
        this.pageName = pageName;
        this.deviceInfoStr = deviceInfoStr;
    }

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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
