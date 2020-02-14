package com.susion.rabbit.base.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * susionwang at 2019-10-25
 */

@Entity
public class RabbitHttpLogInfo implements RabbitInfoProtocol {

    @Id(autoincrement = true)
    public Long id;

    public String host = "";
    public String path = "";
    public String requestBody = "";
    public String responseStr = "";
    public String size = "";
    public String requestType = "";
    public String responseContentType = "gson";
    public String responseCode = "";
    public String requestParamsMapString = "";
    public Long time;
    public Long tookTime;
    public boolean isSuccessRequest;
    public boolean isExceptionRequest;



    public RabbitHttpLogInfo() {
    }

    @Generated(hash = 1095309258)
    public RabbitHttpLogInfo(Long id, String host, String path, String requestBody,
            String responseStr, String size, String requestType,
            String responseContentType, String responseCode,
            String requestParamsMapString, Long time, Long tookTime,
            boolean isSuccessRequest, boolean isExceptionRequest) {
        this.id = id;
        this.host = host;
        this.path = path;
        this.requestBody = requestBody;
        this.responseStr = responseStr;
        this.size = size;
        this.requestType = requestType;
        this.responseContentType = responseContentType;
        this.responseCode = responseCode;
        this.requestParamsMapString = requestParamsMapString;
        this.time = time;
        this.tookTime = tookTime;
        this.isSuccessRequest = isSuccessRequest;
        this.isExceptionRequest = isExceptionRequest;
    }

    public boolean isvalid(){
        return !host.isEmpty() && !path.isEmpty();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestBody() {
        return this.requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseStr() {
        return this.responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getResponseContentType() {
        return this.responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getRequestParamsMapString() {
        return this.requestParamsMapString;
    }

    public void setRequestParamsMapString(String requestParamsMapString) {
        this.requestParamsMapString = requestParamsMapString;
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

    public Long getTookTime() {
        return this.tookTime;
    }

    public void setTookTime(Long tookTime) {
        this.tookTime = tookTime;
    }

    public boolean getIsSuccessRequest() {
        return this.isSuccessRequest;
    }

    public void setIsSuccessRequest(boolean isSuccessRequest) {
        this.isSuccessRequest = isSuccessRequest;
    }

    public boolean getIsExceptionRequest() {
        return this.isExceptionRequest;
    }

    public void setIsExceptionRequest(boolean isExceptionRequest) {
        this.isExceptionRequest = isExceptionRequest;
    }



}
