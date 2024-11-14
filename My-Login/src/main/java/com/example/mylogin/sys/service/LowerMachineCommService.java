package com.example.mylogin.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.mylogin.sys.controller.Result;
import com.example.mylogin.sys.service.Embedded.LinkTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.mylogin.sys.entity.OperationInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import java.util.*;
import org.springframework.web.client.ResourceAccessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
@Service
public class LowerMachineCommService {
    private final Map<String, String> deviceUrlMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(LowerMachineCommService.class);
    private final RestTemplate restTemplate;

    public LowerMachineCommService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // @PostConstruct
    // public void init() {
    //     deviceUrlMap.put("device1", "http://192.168.31.136:12345/heartBeat/info");
    //     deviceUrlMap.put("device2", "http://192.168.31.136:12345/heartBeat/info");
    //     deviceUrlMap.put("device3", "http://192.168.31.136:12345/heartBeat/info");
    //     deviceUrlMap.put("device4", "http://192.168.31.136:12345/heartBeat/info");
    // }

    public List<String> getAllDeviceIds() {
        List<String> deviceIds = new ArrayList<>(deviceUrlMap.keySet());
        logger.info("getAllDeviceIds returning: {}", deviceIds);
        return deviceIds;
    }

    public String getUserInfo(String deviceId) {
        try {
            String url = deviceUrlMap.get(deviceId);
            if (url == null) {
                logger.error("Device ID not found: {}", deviceId);
                return createErrorResponse("Device ID not found");
            }

            OperationInfo info = processSuccessResponse(deviceId, url);
            if (info != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", deviceId);
                result.put("info", info);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(result);
            } else {
                return createErrorResponse("Error processing response");
            }
        } catch (Exception e) {
            logger.error("Error getting user info for device {}: {}", deviceId, e.getMessage());
            return createErrorResponse(e.getMessage());
        }
    }

    private OperationInfo processSuccessResponse(String deviceId, String url) {
        try {
            String url1 = deviceUrlMap.get(deviceId);
            logger.info("Sending request to device {}: {}", deviceId, url1);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url1, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("Received non-OK status code: {}", response.getStatusCode());
                return null;
            }
            
            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                logger.error("Received empty response from device {}", deviceId);
                return null;
            }
            
            logger.debug("Received response: {}", responseBody);
            ObjectMapper mapper = new ObjectMapper();
            
            Result<OperationInfo> result = mapper.readValue(responseBody, 
                new TypeReference<Result<OperationInfo>>() {});
            
            OperationInfo info = result.getData();
            if (info != null) {
                info.removeDuplicates();
            }
            return info;
        } catch (Exception e) {
            logger.error("Error processing response from device {}: {}", deviceId, e.getMessage());
            return null;
        }
    }

    private String createErrorResponse(String message) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("id", null);
            response.put("info", message);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            logger.error("Error creating error response: {}", e.getMessage());
            return "{\"status\":\"error\",\"id\":null,\"info\":\"Internal server error\"}";
        }
    }

    public void updateDeviceUrls(Map<String, String> ipSettings) {
        for (Map.Entry<String, String> entry : ipSettings.entrySet()) {
            String deviceId = entry.getKey();
            String ipAddress = entry.getValue();
            if (ipAddress != null && !ipAddress.isEmpty()) {
                deviceUrlMap.put(deviceId, "http://" + ipAddress + ":12345/lspci");
            }
        }
    }
}
