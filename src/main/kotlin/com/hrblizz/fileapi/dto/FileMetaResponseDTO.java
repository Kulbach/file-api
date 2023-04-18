package com.hrblizz.fileapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class FileMetaResponseDTO {

    private Map<String, FileMetaDTO> files;
}
