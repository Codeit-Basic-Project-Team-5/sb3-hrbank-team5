package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.DataBackup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataBackupRepository extends JpaRepository<DataBackup, Long> {

    List<DataBackup> findAllByWorker(String worker);

}
