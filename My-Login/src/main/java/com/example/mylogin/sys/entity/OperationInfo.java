package com.example.mylogin.sys.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

@Data
public class OperationInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<PCIDevice> pciDevices;

    public void removeDuplicates() {
        if (pciDevices != null) {
            pciDevices = new ArrayList<>(new LinkedHashSet<>(pciDevices));
        }
    }
}