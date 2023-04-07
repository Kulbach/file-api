package com.hrblizz.fileapi.controller;

import com.hrblizz.fileapi.dto.*;
import com.hrblizz.fileapi.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/files", produces = "application/json")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(@Valid @ModelAttribute FileUploadRequestDTO dto){
        FileUploadResponseDTO response = fileService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/files/metas", produces = "application/json")
    public ResponseEntity<FileMetaResponseDTO> getMetas(@RequestBody FileMetaRequestDTO dto) {
        FileMetaResponseDTO response = fileService.getMetas(dto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/file/{token}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String token) {
        FileData fileData = fileService.downloadFile(token);
        return ResponseEntity.ok()
                .header("X-Filename", fileData.getFileName())
                .header("X-Filesize", fileData.getSize().toString())
                .header("X-CreateTime", fileData.getCreateTime().toString())
                .header(HttpHeaders.CONTENT_TYPE, fileData.getContentType())
                .body(fileData.getContent());
    }

    @DeleteMapping("file/{token}")
    public ResponseEntity<Void> deleteFile(@PathVariable String token) {
        fileService.deleteFile(token);
        return ResponseEntity.ok().build();
    }
}
