package com.hrblizz.fileapi.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrblizz.fileapi.controller.exception.InternalException;
import com.hrblizz.fileapi.data.entities.FileEntity;
import com.hrblizz.fileapi.dto.FileUploadDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", builder = @Builder)
public interface FileEntityMapper {

    FileEntityMapper MAPPER = Mappers.getMapper(FileEntityMapper.class);

    @Mapping(target = "fileName", source = "name")
    @Mapping(target = "size", source = "content", qualifiedByName = "getSize")
    @Mapping(target = "source", source = "source")
    @Mapping(target = "expireTime", source = "expireTime")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "meta", source = "meta", qualifiedByName = "parseMeta")
    @Mapping(target = "token", source = "name", qualifiedByName = "generateToken")
    FileEntity mapToEntity(FileUploadDTO dto);

    @Named("parseMeta")
    default Map<String, Object> parseMeta(String meta) {
        try {
           return new ObjectMapper().readValue(meta, new TypeReference<HashMap<String,Object>>() {});
        } catch (JsonProcessingException ex) {
            throw new InternalException("Failed to process meta");
        }
    }

    @Named("generateToken")
    default String generateToken(String originalFileName) {
        return originalFileName + "/" + UUID.randomUUID();
    }

    @Named("getSize")
    default Long getSize(MultipartFile file) {
        return file.getSize();
    }
}
