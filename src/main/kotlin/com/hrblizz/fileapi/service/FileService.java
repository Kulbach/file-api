package com.hrblizz.fileapi.service;

import com.hrblizz.fileapi.dto.FileUploadDTO;
import com.hrblizz.fileapi.dto.FileUploadResponseDTO;

public interface FileService {

    FileUploadResponseDTO save(FileUploadDTO dto);
}
