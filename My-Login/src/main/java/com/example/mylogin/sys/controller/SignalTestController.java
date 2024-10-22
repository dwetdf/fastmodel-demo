package com.example.mylogin.sys.controller;

import com.example.mylogin.sys.service.Embedded.SignalTest;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/test1")  // 更改了基础路径
public class SignalTestController {
    @PostMapping("/FFT")
    public Result<String> FFTById(@RequestParam("id") int id,
                                  @RequestParam("selectedFile") MultipartFile file) throws IOException {
        String FFT = SignalTest.FFTById(id, file);
        return Result.success(Resource(FFT), "success");
    }

    @PostMapping("/FIR")
    public Result<String> FIRById(@RequestParam("id") int id,
                                  @RequestParam("selectedFile") MultipartFile file) throws IOException {
        String FIR = SignalTest.FIRById(id,file);
        return Result.success(Resource(FIR), "success");
    }

    @PostMapping("/FFTA")
    public Result<Map<String, Object>> FFT(@RequestParam("value") String value,
                              @RequestParam("selectedFile") MultipartFile file) throws IOException {
        Map<String, Object> map = new HashMap<>();
        String FFT = SignalTest.FFT(value,file);
        map.put("id",2);
        map.put("file",Resource(FFT));
        return Result.success(map, "success");
    }

    @PostMapping("/FIRA")
    public Result<Map<String, Object>> FIR(@RequestParam("value") String value,
                              @RequestParam("selectedFile") MultipartFile file) throws IOException {
        Map<String, Object> map = new HashMap<>();
        String FIR = SignalTest.FIR(value,file);
        map.put("id",3);
        map.put("file",Resource(FIR));
        return Result.success(map, "success");
    }

    public String Resource(String S) throws IOException {
        File file = new File("src/main/resources/" + S);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + file.getAbsolutePath());
        }
        // 读取文件内容并返回
        return new String(Files.readAllBytes(file.toPath()));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadTestResult(@PathVariable("id") String id) throws IOException {
        System.out.println("Received download request for ID: " + id);
        // Convert id to int if necessary
        int intId = Integer.parseInt(id);
        
        // 根据id获取文件名
        String fileName = "test_result_" + intId + ".txt";
        
        // 获取文件路径
        Path filePath = Paths.get("src/main/resources/" + fileName);

        // 检查文件是否存在，如果不存在则创建
        if (!Files.exists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            // 写入一些默认内容
            String defaultContent = "This is a test result file for ID: " + intId;
            Files.write(filePath, defaultContent.getBytes());
        }

        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
