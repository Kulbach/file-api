package com.hrblizz.fileapi.service.impl;

import com.hrblizz.fileapi.controller.exception.BadRequestException;
import com.hrblizz.fileapi.controller.exception.InternalException;
import com.hrblizz.fileapi.data.entities.FileEntity;
import com.hrblizz.fileapi.data.repository.FileEntityRepository;
import com.hrblizz.fileapi.dto.*;
import com.hrblizz.fileapi.mapper.FileEntityMapper;
import com.hrblizz.fileapi.service.FileService;
import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileEntityRepository fileEntityRepository;
    private final Path uploadPath;

    @Override
    public FileUploadResponseDTO save(FileUploadRequestDTO dto) {
        File file = getFile(dto.getName());

        saveFileLocally(file, dto.getContent());

        return new FileUploadResponseDTO(saveAndGetToken(dto));
    }

    @Override
    public FileMetaResponseDTO getMetas(FileMetaRequestDTO dto) {
        Iterable<FileEntity> fileMetas = findMetas(dto.getTokens());

        Map<String, FileMetaDTO> metasMap =  StreamSupport.stream(fileMetas.spliterator(), false)
                .map(FileEntityMapper.MAPPER::mapToMeta)
                .collect(Collectors.toMap(FileMetaDTO::getToken, Function.identity()));

        return new FileMetaResponseDTO(metasMap);
    }

    @Override
    public FileData downloadFile(String token) {
        FileEntity fileMeta = findMeta(token)
                .orElseThrow(() -> new BadRequestException("There is no file with token " + token));
        File file = getFile(fileMeta.getFileName());
        InputStreamResource resource = getFileContent(file);

        return FileEntityMapper.MAPPER.mapToFileData(fileMeta, resource);
    }

    @Override
    public void deleteFile(String token) {
        FileEntity fileMeta = findMeta(token)
                .orElseThrow(() -> new BadRequestException("There is no file with token " + token));
        File file = getFile(fileMeta.getFileName());
        deleteFileLocally(file);
        deleteFileFromDB(token);
    }

    private String saveAndGetToken(FileUploadRequestDTO dto) {
        FileEntity entity = saveFileToDB(dto);
        return entity.getToken();
    }

    private FileEntity saveFileToDB(FileUploadRequestDTO dto) {
        FileEntity fileEntity = FileEntityMapper.MAPPER.mapToEntity(dto);

        try {
            fileEntityRepository.save(fileEntity);
            return fileEntity;
        } catch (MongoException e) {
            throw new InternalException("Failed to store file in mongodb " + fileEntity.toString(), e);
        }
    }

    private void deleteFileFromDB(String token) {
        try {
            fileEntityRepository.deleteById(token);
        } catch (MongoException e) {
            throw new InternalException("Failed to delete meta from mongodb for token " + token, e);
        }
    }

    private void saveFileLocally(File file, MultipartFile content) {
        if (file.exists()) {
            throw new BadRequestException("File already exists on disk " + file.getName());
        }

        try {
            OutputStream outputStream = Files.newOutputStream(file.toPath());
            outputStream.write(content.getBytes());
        } catch (IOException e) {
            throw new InternalException("Failed to save file on disk " + file.getName(), e);
        }
    }

    private File getFile(String fileName) {
        return new File(uploadPath + "/" + fileName);
    }

    private InputStreamResource getFileContent(File file) {
        try {
            return new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new InternalException("File does not exist " + file.getName(), e);
        }
    }

    private void deleteFileLocally(File file) {
        if (!file.delete()) {
            throw new InternalException("Failed to delete file with name " + file.getName());
        }
    }

    private Iterable<FileEntity> findMetas(List<String> tokens) {
        try {
            return fileEntityRepository.findAllById(tokens);
        } catch (MongoException e) {
            throw new InternalException("Failed to get metas by list of tokens " + tokens, e);
        }
    }

    private Optional<FileEntity> findMeta(String token) {
        try {
            return fileEntityRepository.findById(token);
        } catch (MongoException e) {
            throw new InternalException("Failed to get meta by token " + token, e);
        }
    }
}
