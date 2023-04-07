package com.hrblizz.fileapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class FileUploadDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String contentType;
    @NotBlank
    private String meta;
    @NotBlank
    private String source;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
    @NotNull
    private MultipartFile content;
}
