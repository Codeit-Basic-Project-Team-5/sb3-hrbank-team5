package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.ChangeLogDiff;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogDiffRepository extends JpaRepository<ChangeLogDiff, Long> {

    List<ChangeLogDiff> findByChangeLogIdOrderByCreatedAtDesc(Long changeLogId);
}
