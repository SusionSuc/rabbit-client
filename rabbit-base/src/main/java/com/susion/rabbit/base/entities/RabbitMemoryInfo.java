package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * susionwang at 2019-12-03
 */
@Entity
public class RabbitMemoryInfo  {

    @Id(autoincrement = true)
    public Long id;

    public long time;

    public int totalSize;

    public int vmSize;

    public int nativeSize;

    public int othersSize;

    @Generated(hash = 506538862)
    public RabbitMemoryInfo(Long id, long time, int totalSize, int vmSize,
            int nativeSize, int othersSize) {
        this.id = id;
        this.time = time;
        this.totalSize = totalSize;
        this.vmSize = vmSize;
        this.nativeSize = nativeSize;
        this.othersSize = othersSize;
    }

    @Generated(hash = 1300157943)
    public RabbitMemoryInfo() {
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

    public int getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getVmSize() {
        return this.vmSize;
    }

    public void setVmSize(int vmSize) {
        this.vmSize = vmSize;
    }

    public int getNativeSize() {
        return this.nativeSize;
    }

    public void setNativeSize(int nativeSize) {
        this.nativeSize = nativeSize;
    }

    public int getOthersSize() {
        return this.othersSize;
    }

    public void setOthersSize(int othersSize) {
        this.othersSize = othersSize;
    }


}
