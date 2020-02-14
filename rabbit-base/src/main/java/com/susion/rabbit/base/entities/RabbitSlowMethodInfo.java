package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * susionwang at 2020-01-02
 */
@Entity
public class RabbitSlowMethodInfo implements RabbitInfoProtocol{

    @Id(autoincrement = true)
    public Long id;

    public Long time;

    public String pkgName;

    public String className;

    public String methodName;

    public Long costTimeMs;

    public String callStack;

    public String pageName;

    @Generated(hash = 367496386)
    public RabbitSlowMethodInfo(Long id, Long time, String pkgName,
            String className, String methodName, Long costTimeMs, String callStack,
            String pageName) {
        this.id = id;
        this.time = time;
        this.pkgName = pkgName;
        this.className = className;
        this.methodName = methodName;
        this.costTimeMs = costTimeMs;
        this.callStack = callStack;
        this.pageName = pageName;
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

    @Override
    public String getPageName() {
        return pageName;
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

    public String getCallStack() {
        return this.callStack;
    }

    public void setCallStack(String callStack) {
        this.callStack = callStack;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

}
