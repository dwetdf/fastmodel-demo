package com.example.mylogin.sys.entity;
import java.io.Serializable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.mylogin.sys.service.DeviceInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 
 * </p>
 *
 * @author wow
 * @since 2024-04-15
 */

public class OperationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cpu;
    private List<String> mermoyList;
    private List<String> gpuList;

    public OperationInfo(){

    }

    public OperationInfo(String cpu, List<String> mermoyList, List<String> gpuList) {
        this.cpu = cpu;
        this.mermoyList = mermoyList;
        this.gpuList = gpuList;
    }


    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public List<String> getMermoyList() {
        return mermoyList;
    }

    public void setMermoyList(List<String> mermoyList) {
        this.mermoyList = mermoyList;
    }

    public List<String> getGpuList() {
        return gpuList;
    }

    public void setGpuList(List<String> gpuList) {
        this.gpuList = gpuList;
    }

    @Override
    public String toString() {
        return "OperationInfo{" +
                "cpu=" + cpu +
                ", mermoyList=" + mermoyList +
                ", gpuList=" + gpuList +
                '}';
    }
    public static OperationInfo fromString(String infoString) throws JsonProcessingException {
        OperationInfo info = new OperationInfo();
        ObjectMapper mapper = new ObjectMapper();
        info = mapper.readValue(infoString, OperationInfo.class);
        return info;
    }
}
