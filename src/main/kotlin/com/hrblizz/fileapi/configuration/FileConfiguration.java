package com.hrblizz.fileapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileConfiguration {

    @Value("${file.upload.dir}")
    private String uploadFileDir;

    @Bean
    public Path uploadPath() {
        return Paths.get(uploadFileDir);
    }
}
