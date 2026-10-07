package com.enterprise.admin.controller;

import com.enterprise.admin.common.BusinessException;
import com.enterprise.admin.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传接口
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    /** 上传文件 */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        // 获取原文件扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 生成新文件名：UUID + 原扩展名
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(uploadPath, fileName);
        // 确保目录存在
        File parentDir = dest.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            file.transferTo(dest.getAbsoluteFile());
        } catch (Exception e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
        Map<String, String> data = new HashMap<>();
        data.put("url", "/uploads/" + fileName);
        data.put("fileName", fileName);
        return Result.success(data);
    }
}
