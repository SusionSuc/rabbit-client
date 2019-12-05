package com.susion.rabbit.exception.entities;

import com.susion.rabbit.base.entities.RabbitGreenDaoInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * susionwang at 2019-10-25
 */

@Entity
public class RabbitExceptionInfo implements RabbitGreenDaoInfo {

    @Id(autoincrement = true)
    public Long id;
    public String crashTraceStr;
    public String simpleMessage;
    public String threadName;
    public String currentSystemVersion;
    public String exceptionName;
    public Long time;

    public RabbitExceptionInfo() {
    }

    @Generated(hash = 2118408603)
    public RabbitExceptionInfo(Long id, String crashTraceStr, String simpleMessage,
            String threadName, String currentSystemVersion, String exceptionName,
            Long time) {
        this.id = id;
        this.crashTraceStr = crashTraceStr;
        this.simpleMessage = simpleMessage;
        this.threadName = threadName;
        this.currentSystemVersion = currentSystemVersion;
        this.exceptionName = exceptionName;
        this.time = time;
    }

    public boolean isvalid(){
        return !crashTraceStr.isEmpty();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrashTraceStr() {
        return this.crashTraceStr;
    }

    public void setCrashTraceStr(String crashTraceStr) {
        this.crashTraceStr = crashTraceStr;
    }

    public String getSimpleMessage() {
        return this.simpleMessage;
    }

    public void setSimpleMessage(String simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getCurrentSystemVersion() {
        return this.currentSystemVersion;
    }

    public void setCurrentSystemVersion(String currentSystemVersion) {
        this.currentSystemVersion = currentSystemVersion;
    }

    public String getExceptionName() {
        return this.exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Keep
    @Override
    public String getSortField() {
        return "time";
    }

    @Override
    public long getLongTime() {
        return time;
    }

}
