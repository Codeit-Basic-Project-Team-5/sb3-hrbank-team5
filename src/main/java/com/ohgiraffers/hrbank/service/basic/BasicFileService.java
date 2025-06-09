package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.entity.File;
import com.ohgiraffers.hrbank.repository.FileRepository;
import com.ohgiraffers.hrbank.storage.FileStorage;
import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicFileService implements FileService {
    private final FileRepository fileRepository;
    private final FileStorage fileStorage;

    @Transactional
    @Override
    public File create(FileCreateRequest fileCreateRequest) {
        byte[] bytes = fileCreateRequest.bytes();
        String fileName = fileCreateRequest.fileName();
        String contentType = fileCreateRequest.contentType();
        String extension = extractExtension(fileName);

        // 파일 메타데이터 저장
        File file = new File(fileName, contentType, (long) bytes.length);
        File savedFile = fileRepository.save(file);

        // 실제 파일 저장
        try (OutputStream out = fileStorage.put(savedFile.getId(), extension)) {
            out.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + fileName, e);
        }

        return savedFile;
    }

    @Override
    public ResponseEntity<Resource> download(Long fileId) {
        File file = fileRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다. id=" + fileId));

        String extension = extractExtension(file.getName());
        return fileStorage.download(file, extension);
    }

    private String extractExtension(String fileName) {
        int dotIdx = fileName.lastIndexOf(".");
        return (dotIdx != -1) ? fileName.substring(dotIdx) : "";
    }
}