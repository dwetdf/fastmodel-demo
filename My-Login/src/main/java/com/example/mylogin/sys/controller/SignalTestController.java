package com.example.mylogin.sys.controller;

import com.example.mylogin.sys.service.Embedded.SignalTest;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Base64;

@RestController
@RequestMapping("/test1")  // 更改了基础路径
public class SignalTestController {
    @PostMapping("/FFT")
    public Result<Map<String, Object>> FFTById(@RequestParam("id") int id,
                                              @RequestParam("selectedFile") MultipartFile file) throws IOException {
    
        Map<String, Object> map = new HashMap<>();
        String FFT = SignalTest.FFTById(id, file);
        System.out.println("FFT result file path: " + FFT);
        map.put("id", 1);
        byte[] fileContent = Files.readAllBytes(Paths.get(FFT));
        map.put("fileContent", Base64.getEncoder().encodeToString(fileContent)); // 直接存储文件路径
        return Result.success(map, "success");
    }

    @PostMapping("/FIR")
    public Result<Map<String, Object>> FIR(@RequestParam("id") int id,
                                           @RequestParam("selectedFile") MultipartFile file) throws IOException {
        Map<String, Object> map = new HashMap<>();
        String FIR = SignalTest.FIRById(id, file);
        map.put("id", 2);
        byte[] fileContent = Files.readAllBytes(Paths.get(FIR));
        map.put("fileContent", Base64.getEncoder().encodeToString(fileContent)); // 直接存储文件路径
        return Result.success(map, "success");
    }

    @PostMapping("/FFTA")
    public Result<Map<String, Object>> FFT(@RequestParam("value") String value,
                              @RequestParam("selectedFile") MultipartFile file) throws IOException {
        System.out.println("FFT method called with value: " + value);
        System.out.println("Uploaded file name: " + file.getOriginalFilename());
        
        Map<String, Object> map = new HashMap<>();
        String FFT = SignalTest.FFT(value, file);
        
        System.out.println("FFT result file path: " + FFT);
        
        Path path = Paths.get(FFT);
        String fileContent = Files.readString(path);
        
        System.out.println("File content length: " + fileContent.length());
        System.out.println("First 100 characters of file content: " + 
                           fileContent.substring(0, Math.min(100, fileContent.length())));
        
        map.put("id", 1);
        map.put("fileContent", fileContent);
        
        return Result.success(map, "success");
    }

    @PostMapping("/FIRA")
    public Result<Map<String, Object>> FIRA(@RequestParam("value") String value,
                              @RequestParam("selectedFile") MultipartFile file) throws IOException {
        System.out.println("FIRA method called with value: " + value);
        
        Map<String, Object> map = new HashMap<>();
        String FIR = SignalTest.FIR(value, file);
        
        System.out.println("FIR result file path: " + FIR);
        
        map.put("id", 2);
        Path path = Paths.get(FIR);
        
        String fileContent = Files.readString(path);
        
        System.out.println("File content length: " + fileContent.length());
        System.out.println("First 100 characters of file content: " + fileContent.substring(0, Math.min(100, fileContent.length())));
        
        map.put("fileContent", fileContent);
        return Result.success(map, "success");
    }

    public String readResourceContent(String S) throws IOException {
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

    @GetMapping("/resource")
    public ResponseEntity<Resource> getResource(@RequestParam String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    private String readFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
