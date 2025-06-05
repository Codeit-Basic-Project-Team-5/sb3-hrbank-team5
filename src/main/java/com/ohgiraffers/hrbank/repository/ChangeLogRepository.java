package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

}
