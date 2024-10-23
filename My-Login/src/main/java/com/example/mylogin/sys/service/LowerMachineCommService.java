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
        String url = deviceUrlMap.get(deviceId);
        if (url == null) {
            throw new IllegalArgumentException("Device ID not found: " + deviceId);
        }

        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                
                if (response.getStatusCode() == HttpStatus.OK) {
                    // return response.getBody();
                    // String result = response.getBody();
                    OperationInfo info = processSuccessResponse(deviceId, url);
                    if (info != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("id", deviceId);
                        result.put("info", info);
                        ObjectMapper mapper = new ObjectMapper();
                        String res=mapper.writeValueAsString(result);
                        System.out.println(res);
                        return res;
                    } else {
                        return "Error: Error processing response";// 处理错误情况
                    }
                } else {
                    logger.error("Failed to get user info for device {}. Status code: {}", deviceId, response.getStatusCode());
                    return "Error: Failed to get user info with status code " + response.getStatusCode();
                }
            } catch (ResourceAccessException e) {
                logger.warn("Connection failed for device {}, retrying... (Attempt {} of {})", deviceId, retryCount + 1, maxRetries);
                retryCount++;
                if (retryCount >= maxRetries) {
                    logger.error("Failed to get user info for device {} after {} attempts", deviceId, maxRetries, e);
                    return "Error: Connection failed after multiple attempts";
                }
                try {
                    Thread.sleep(1000); // Wait for 1 second before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "Error: Retry interrupted";
                }
            } catch (Exception e) {
                logger.error("Failed to get user info for device {}", deviceId, e);
                return "Error: " + e.getMessage();
            }
        }
        return "Error: Unexpected error occurred";
    }

    private OperationInfo processSuccessResponse(String deviceId, String url) {
        String url1 = deviceUrlMap.get(deviceId);
        logger.info("Received successful response from device {}: {}", deviceId, url);
        String response = restTemplate.getForObject(url1, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Result<OperationInfo> userResult = mapper.readValue(response, mapper.getTypeFactory().constructParametricType(
                    Result.class,
                    OperationInfo.class
            ));
            return userResult.getData();
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response from device {}", deviceId, e);
            return null;
        }
    }

    public void updateDeviceUrls(Map<String, String> ipSettings) {
        for (Map.Entry<String, String> entry : ipSettings.entrySet()) {
            String deviceId = entry.getKey();
            String ipAddress = entry.getValue();
            if (ipAddress != null && !ipAddress.isEmpty()) {
                deviceUrlMap.put(deviceId, "http://" + ipAddress + ":12345/heartBeat/info");
            }
        }
    }
}
