package com.dhu.smartmed.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有路径
                .allowedOrigins("http://localhost:5173") // 前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true) // 允许携带 cookie
                .allowedHeaders("*")
                .maxAge(3600); // 预检请求有效期1小时
    }
}
