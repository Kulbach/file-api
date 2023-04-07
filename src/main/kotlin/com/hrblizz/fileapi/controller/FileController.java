package com.hrblizz.fileapi.controller;

import com.hrblizz.fileapi.dto.FileMetaRequestDTO;
import com.hrblizz.fileapi.dto.FileMetaResponseDTO;
import com.hrblizz.fileapi.dto.FileUploadRequestDTO;
import com.hrblizz.fileapi.dto.FileUploadResponseDTO;
import com.hrblizz.fileapi.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
