package com.classpi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Component
public class EnvConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        loadEnv();
    }

    public void loadEnv() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            envFile = new File("classpi-backend/.env");
        }

        if (!envFile.exists()) {
            envFile = new File(System.getProperty("user.dir"), ".env");
        }

        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        int equalsIndex = line.indexOf('=');
                        if (equalsIndex > 0) {
                            String key = line.substring(0, equalsIndex).trim();
                            String value = line.substring(equalsIndex + 1).trim();
                            System.setProperty(key, value);
                            log.info("Loaded environment variable: {}", key);
                        }
                    }
                }
            } catch (IOException e) {
                log.warn("Failed to load .env file: {}", e.getMessage());
            }
        }
    }
}