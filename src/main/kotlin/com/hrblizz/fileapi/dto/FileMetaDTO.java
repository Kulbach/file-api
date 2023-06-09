package com.hrblizz.fileapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class FileMetaDTO {

    private String token;
    private String fileName;
    private Long size;
    private String contentType;
    private LocalDateTime createTime;
    private String source;
    private Map<String, Object> meta;
}
