package com.susion.rabbit.base.entities;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * susionwang at 2019-12-05
 */
@Entity
public class RabbitReportInfo implements RabbitInfoProtocol{

    @Id(autoincrement = true)
    public Long id;

    @SerializedName("info_str")
    public String infoStr;

    public Long time;

    @SerializedName("device_info_str")
    public String deviceInfoStr;

    public String type;

    @SerializedName("use_time")
    public long useTime;

    @Keep
    public RabbitReportInfo(String infoStr, Long time,
                            String deviceInfoStr, String type, long appUseTime) {
        this.infoStr = infoStr;
        this.time = time;
        this.deviceInfoStr = deviceInfoStr;
        this.type = type;
        this.useTime = appUseTime;
    }

    @Generated(hash = 1889523049)
    public RabbitReportInfo(Long id, String infoStr, Long time,
                            String deviceInfoStr, String type, long useTime) {
        this.id = id;
        this.infoStr = infoStr;
        this.time = time;
        this.deviceInfoStr = deviceInfoStr;
        this.type = type;
        this.useTime = useTime;
    }

    @Generated(hash = 2125059637)
    public RabbitReportInfo() {
    }

    /**
     * 必须是这两个字段
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RabbitReportInfo that = (RabbitReportInfo) o;
        return Objects.equals(time, that.time) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, type);
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

    @Override
    public String getPageName() {
        return "";
    }

    public void setTime(Long time) {
        this.time = time;
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

    public long getUseTime() {
        return this.useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }


}
