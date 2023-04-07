package com.hrblizz.fileapi.service;

import com.hrblizz.fileapi.dto.FileMetaRequestDTO;
import com.hrblizz.fileapi.dto.FileMetaResponseDTO;
import com.hrblizz.fileapi.dto.FileUploadRequestDTO;
import com.hrblizz.fileapi.dto.FileUploadResponseDTO;

public interface FileService {

    FileUploadResponseDTO save(FileUploadRequestDTO dto);

    FileMetaResponseDTO getMetas(FileMetaRequestDTO dto);
}
