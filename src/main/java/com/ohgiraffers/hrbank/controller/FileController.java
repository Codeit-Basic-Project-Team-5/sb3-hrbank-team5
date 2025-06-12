package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.entity.File;
import com.ohgiraffers.hrbank.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File API", description = "파일 관련 api")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    //테스트용
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> upload(@RequestPart("file") MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            byte[] bytes = multipartFile.getBytes();

            FileCreateRequest request = new FileCreateRequest(fileName, contentType, bytes);
            File saved = fileService.create(request);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved.getId());

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 처리 중 오류가 발생했습니다.", e);
        }
    }

    @GetMapping(
        path = "/{id}/download",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        return fileService.download(id);
    }
}