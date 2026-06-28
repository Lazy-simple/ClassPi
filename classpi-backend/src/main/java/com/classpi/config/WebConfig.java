package com.classpi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /api/file/static/** 到 E:/upload/
        registry.addResourceHandler("/api/file/static/**")
                .addResourceLocations("file:E:/upload/");
    }
}