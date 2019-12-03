package com.susion.rabbit.performance.entities;

import com.susion.rabbit.base.entities.RabbitGreenDaoInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2019-12-03
 */
@Entity
public class RabbitMemoryInfo implements RabbitGreenDaoInfo {

    @Id(autoincrement = true)
    public Long id;

    public long time;

    public int memorySize;

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


    public long getTime() {
        return this.time;
    }


    public void setTime(long time) {
        this.time = time;
    }


    public int getMemorySize() {
        return this.memorySize;
    }


    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }


    public RabbitMemoryInfo(long time, int memorySize) {
        this.time = time;
        this.memorySize = memorySize;
    }


    @Generated(hash = 1028915299)
    public RabbitMemoryInfo(Long id, long time, int memorySize) {
        this.id = id;
        this.time = time;
        this.memorySize = memorySize;
    }


    @Generated(hash = 1300157943)
    public RabbitMemoryInfo() {
    }

}
