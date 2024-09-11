package com.example.mylogin.sys.service.Embedded;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class YOLO {

    // 方法用于执行YOLO检测
    public static String runYoloDetection(MultipartFile file) throws IOException {
        String videoOutPath = "C:\\yolo\\output.mp4";
        String uploadDir = "C:\\yolo\\";
            // 创建目标文件
        String videoInPath = uploadDir + file.getOriginalFilename();
        System.out.println(videoInPath);
        System.out.println(videoOutPath);
        File targetFile = new File(uploadDir + file.getOriginalFilename());
            // 保存文件
        file.transferTo(targetFile);
        try {
            // 使用临时文件路径作为参数传递给ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder("D:\\anaconda3\\envs\\pytorch\\python.exe", "C:\\yolo\\main.py", videoInPath, videoOutPath);
            // 构建Python命令的参数列表，传递视频文件路径作为参数

            Process process = pb.start();

            // 读取Python脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 读取Python脚本的错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.out.println("Python Error: " + line);
            }
            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Python ending: " + exitCode);

        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }return videoOutPath;
    }
}


