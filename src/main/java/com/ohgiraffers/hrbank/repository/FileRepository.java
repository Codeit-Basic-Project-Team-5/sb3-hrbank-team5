package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.File;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findById(Long id);
}