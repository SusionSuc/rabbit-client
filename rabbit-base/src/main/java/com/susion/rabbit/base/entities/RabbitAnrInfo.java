package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2020-03-17
 */
@Entity
public class RabbitAnrInfo implements RabbitInfoProtocol {

    @Id(autoincrement = true)
    public Long id;

    public Long time;

    public String stackStr;

    public String pageName;

    public boolean invalid;

    public String filePath;


    @Generated(hash = 1719305380)
    public RabbitAnrInfo(Long id, Long time, String stackStr, String pageName,
            boolean invalid, String filePath) {
        this.id = id;
        this.time = time;
        this.stackStr = stackStr;
        this.pageName = pageName;
        this.invalid = invalid;
        this.filePath = filePath;
    }

    @Generated(hash = 342101226)
    public RabbitAnrInfo() {
    }


    @Override
    public Long getTime() {
        return time;
    }

    @Override
    public String getPageName() {
        return pageName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getStackStr() {
        return this.stackStr;
    }

    public void setStackStr(String stackStr) {
        this.stackStr = stackStr;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public boolean getInvalid() {
        return this.invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }




}
