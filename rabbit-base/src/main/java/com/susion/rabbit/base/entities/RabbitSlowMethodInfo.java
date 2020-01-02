package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2020-01-02
 */
@Entity
public class RabbitSlowMethodInfo {

    @Id(autoincrement = true)
    public Long id;

    public Long time;

    public String pkgName;

    public String className;

    public String methodName;

    public Long costTimeMs;

    @Generated(hash = 609049470)
    public RabbitSlowMethodInfo(Long id, Long time, String pkgName, String className, String methodName,
            Long costTimeMs) {
        this.id = id;
        this.time = time;
        this.pkgName = pkgName;
        this.className = className;
        this.methodName = methodName;
        this.costTimeMs = costTimeMs;
    }

    public RabbitSlowMethodInfo(Long time, String pkgName, String className, String methodName, Long costTimeMs) {
        this.time = time;
        this.pkgName = pkgName;
        this.className = className;
        this.methodName = methodName;
        this.costTimeMs = costTimeMs;
    }

    @Generated(hash = 1558201019)
    public RabbitSlowMethodInfo() {
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

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getCostTimeMs() {
        return this.costTimeMs;
    }

    public void setCostTimeMs(Long costTimeMs) {
        this.costTimeMs = costTimeMs;
    }

}
