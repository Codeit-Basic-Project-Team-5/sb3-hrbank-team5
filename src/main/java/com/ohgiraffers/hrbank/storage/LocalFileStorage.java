package com.ohgiraffers.hrbank.storage;

import com.ohgiraffers.hrbank.entity.File;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    prefix = "hrbank.storage",
    name = "type",
    havingValue = "local"
)
public class LocalFileStorage implements FileStorage {

    private final Path root;

    public LocalFileStorage(
        @Value("${discodeit.storage.local.root-path}") String rootPath
    ) {
        this.root = Paths.get(rootPath);
    }

    private Path resolvePath(Long id, String extension) {
        return root.resolve(id.toString() + extension);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException(
                "디렉토리를 초기화할 수 없습니다: " + root, e);
        }
    }

    @Override
    public OutputStream put(Long id, String extension) {
        Path path = resolvePath(id, extension);
        try {
            return new FileOutputStream(path.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("파일 ID " + id + "에 대한 출력 스트림을 열 수 없습니다.", e);
        }
    }

    @Override
    public InputStream get(Long id, String extension) {
        Path path = resolvePath(id, extension);
        try {
            return new FileInputStream(path.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("파일 ID " + id + "에 대한 입력 스트림을 열 수 없습니다.", e);
        }
    }

    //Dto 수정
    @Override
    public ResponseEntity<Resource> download(File file, String extension) {
        Path path = resolvePath(file.getId(), extension);
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("파일을 읽을 수 없습니다: " + path);
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException("리소스를 생성할 수 없습니다: " + path, e);
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
            .contentType(MediaType.parseMediaType(file.getType()))
            .contentLength(file.getSize())
            .body(resource);
    }
}
