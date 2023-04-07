package com.hrblizz.fileapi.data.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Document("files")
@Data
@Builder
public class FileEntity {

    @Id
    private String token;
    private String fileName;
    private Long size;
    private String source;
    private String contentType;
    private LocalDateTime expireTime;
    private Map<String, Object> meta;
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @Version
    public Integer version;
}
