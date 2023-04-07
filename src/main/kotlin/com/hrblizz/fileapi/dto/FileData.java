package com.hrblizz.fileapi.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.InputStreamResource;

import java.time.LocalDateTime;

@Data
@Builder
public class FileData {

    private InputStreamResource content;
    private String contentType;
    private String fileName;
    private LocalDateTime createTime;
    private Long size;
}
