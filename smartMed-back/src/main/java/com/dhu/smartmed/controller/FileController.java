package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@Component
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.access.path:uploads}")
    private String accessPath;

    /**
     * 文件上传
     * 与前端调用格式保持一致
     */
    @PostMapping("/upload")
    public RespResult uploadFile(@RequestParam("file") MultipartFile file) {
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String originalFilename = file.getOriginalFilename();
        String suffix = StringUtils.getFilenameExtension(originalFilename);
        if (suffix == null || suffix.isEmpty()) {
            return RespResult.fail("文件格式错误");
        }
        String newFilename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
        Path saveDirPath = Paths.get(uploadPath, dateDir);
        Path fullFilePath = saveDirPath.resolve(newFilename);

        try {
            Files.createDirectories(saveDirPath);
            System.out.println("准备保存文件到: " + fullFilePath.toAbsolutePath());
            System.out.println("文件大小: " + file.getSize());
            file.transferTo(fullFilePath.toFile());
            System.out.println("文件是否存在: " + fullFilePath.toFile().exists());
        } catch (Exception e) {
            e.printStackTrace();
            return RespResult.fail("上传失败: " + e.getMessage());
        }

        String fileUrl = "/" + accessPath + "/" + dateDir + "/" + newFilename;
        return RespResult.success("上传成功", fileUrl);
    }


    /**
     * 检查文件类型是否是图片
     */
    private boolean isImageFile(String filename) {
        if (filename == null) {
            return false;
        }
        filename = filename.toLowerCase();
        return filename.endsWith(".jpg") ||
                filename.endsWith(".jpeg") ||
                filename.endsWith(".png") ||
                filename.endsWith(".gif") ||
                filename.endsWith(".bmp");
    }
}