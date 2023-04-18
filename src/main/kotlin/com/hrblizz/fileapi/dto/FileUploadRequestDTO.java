package com.hrblizz.fileapi.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class FileUploadRequestDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String contentType;
    @NotBlank
    private String meta;
    @NotBlank
    private String source;
    @NotNull
    private MultipartFile content;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime expireTime;
}
