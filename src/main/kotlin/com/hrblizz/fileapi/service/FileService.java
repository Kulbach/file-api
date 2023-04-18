package com.hrblizz.fileapi.service;

import com.hrblizz.fileapi.dto.*;

public interface FileService {

    FileUploadResponseDTO save(FileUploadRequestDTO dto);

    FileMetaResponseDTO getMetas(FileMetaRequestDTO dto);

    FileData downloadFile(String token);

    void deleteFile(String token);
}
