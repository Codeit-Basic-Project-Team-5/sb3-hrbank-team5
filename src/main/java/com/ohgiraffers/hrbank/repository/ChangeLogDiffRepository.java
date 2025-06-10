package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.ChangeLogDiff;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogDiffRepository extends JpaRepository<ChangeLogDiff, Long> {

    List<ChangeLogDiff> findByChangeLogIdOrderByCreatedAtDesc(Long changeLogId);
}