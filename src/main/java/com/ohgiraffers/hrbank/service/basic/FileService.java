package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.entity.File;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface FileService {
    File create(FileCreateRequest fileCreateRequest);
    ResponseEntity<Resource> download(Long fileId);
}
