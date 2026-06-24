package com.classpi.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.classpi.mapper")
public class MyBatisPlusConfig {
}
