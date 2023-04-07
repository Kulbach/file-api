package com.hrblizz.fileapi.controller;

import com.hrblizz.fileapi.dto.FileUploadDTO;
import com.hrblizz.fileapi.dto.FileUploadResponseDTO;
import com.hrblizz.fileapi.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/files", produces = "application/json")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(@Valid @ModelAttribute FileUploadDTO dto){
        FileUploadResponseDTO response = fileService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
