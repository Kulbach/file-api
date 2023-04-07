package com.hrblizz.fileapi.service.impl;

import com.hrblizz.fileapi.controller.exception.BadRequestException;
import com.hrblizz.fileapi.controller.exception.InternalException;
import com.hrblizz.fileapi.data.entities.FileEntity;
import com.hrblizz.fileapi.data.repository.FileEntityRepository;
import com.hrblizz.fileapi.dto.FileUploadDTO;
import com.hrblizz.fileapi.dto.FileUploadResponseDTO;
import com.hrblizz.fileapi.mapper.FileEntityMapper;
import com.hrblizz.fileapi.service.FileService;
import com.hrblizz.fileapi.service.impl.exception.ProcessException;
import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${file.upload.dir}")
    private String uploadFileDir;

    private final FileEntityRepository fileEntityRepository;

    public FileUploadResponseDTO save(FileUploadDTO dto) {
        Path path = Paths.get(uploadFileDir);
        File file = new File(path + "/" + dto.getName());

        try {
            saveFileLocally(file, dto.getContent());
            return new FileUploadResponseDTO(saveAndGetToken(dto));
        } catch (Exception ex) {
            if (file.exists()) {
                file.delete();
            }
            throw new InternalException(ex.getLocalizedMessage());
        }
    }

    private String saveAndGetToken(FileUploadDTO dto) {
        FileEntity entity = saveFileToDB(dto);
        return entity.getToken();
    }

    private void saveFileLocally(File file, MultipartFile content) {
        if (file.exists()) {
            throw new BadRequestException("File already exists");
        }

        try {
            OutputStream outputStream = Files.newOutputStream(file.toPath());
            outputStream.write(content.getBytes());
        } catch (IOException ex) {
            throw new ProcessException("Failed to save file");
        }
    }

    private FileEntity saveFileToDB(FileUploadDTO dto) {
        FileEntity fileEntity = FileEntityMapper.MAPPER.mapToEntity(dto);

        try {
            fileEntityRepository.save(fileEntity);
            return fileEntity;
        } catch (MongoException ex) {
            throw new ProcessException("Failed to store file in mongodb");
        }
    }
}
