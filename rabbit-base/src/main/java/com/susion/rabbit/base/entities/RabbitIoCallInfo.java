package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2020-01-08
 */
@Entity
public class RabbitIoCallInfo implements RabbitInfoProtocol{

    @Id(autoincrement = true)
    public Long id;

    public String invokeStr;

    public String becalledStr;

    public Long time;

    @Generated(hash = 135502539)
    public RabbitIoCallInfo(Long id, String invokeStr, String becalledStr,
            Long time) {
        this.id = id;
        this.invokeStr = invokeStr;
        this.becalledStr = becalledStr;
        this.time = time;
    }

    @Generated(hash = 1556471498)
    public RabbitIoCallInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvokeStr() {
        return this.invokeStr;
    }

    public void setInvokeStr(String invokeStr) {
        this.invokeStr = invokeStr;
    }

    public String getBecalledStr() {
        return this.becalledStr;
    }

    public void setBecalledStr(String becalledStr) {
        this.becalledStr = becalledStr;
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

}
