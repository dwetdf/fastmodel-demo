package com.example.mylogin.sys.service;

import com.example.mylogin.sys.entity.OperationInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class DeviceStatus {
    private String id;
    private String status;
    private OperationInfo info;

    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OperationInfo getInfo() { return info != null ? info : new OperationInfo(); }
    public void setInfo(OperationInfo info) { this.info = info; }
}
