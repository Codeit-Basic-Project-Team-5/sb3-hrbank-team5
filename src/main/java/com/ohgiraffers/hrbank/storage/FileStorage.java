package com.ohgiraffers.hrbank.storage;

import com.ohgiraffers.hrbank.entity.File;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface FileStorage {
    OutputStream put(Long id, String extension);

    InputStream get(Long id, String extension);

    ResponseEntity<Resource> download(File file, String extension);
}
