package com.dhu.smartmed.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.access.path}")
    private String accessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 规范路径格式，确保结尾带 /
        String normalizedPath = uploadPath.replace("\\", "/");
        if (!normalizedPath.endsWith("/")) {
            normalizedPath += "/";
        }

        // 添加资源映射
        registry.addResourceHandler("/" + accessPath + "/**")
                .addResourceLocations("file:" + normalizedPath);
    }
}


