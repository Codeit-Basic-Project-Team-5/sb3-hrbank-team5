package com.ohgiraffers.hrbank.service.basic;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.ohgiraffers.hrbank.dto.data.BackupDto;
import com.ohgiraffers.hrbank.dto.request.BackupCursorPageRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseBackupDto;
import com.ohgiraffers.hrbank.entity.Backup;
import com.ohgiraffers.hrbank.entity.ChangeLog;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.File;
import com.ohgiraffers.hrbank.entity.StatusType;
import com.ohgiraffers.hrbank.mapper.BackupMapper;
import com.ohgiraffers.hrbank.repository.BackupRepository;
import com.ohgiraffers.hrbank.repository.ChangeLogRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.repository.FileRepository;
import com.ohgiraffers.hrbank.storage.FileStorage;
import com.ohgiraffers.hrbank.service.BackupService;
import com.ohgiraffers.hrbank.storage.FileStorage;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicBackupService implements BackupService {

    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;
    private final ChangeLogRepository changeLogRepository;
    private final FileRepository fileRepository;
    private final FileStorage fileStorage;
    private final EmployeeRepository employeeRepository;
    private @Value("${hr-bank.storage.local.root-path}") String root;

    /**
     * dataBackupRepository에서 모든 항목을 검색하여 Dto로 반환
     * @return List<DataBackupDto> 데이터베이스 내의 모든 DataBackup 자료 반환
     */
    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseBackupDto findAll(BackupCursorPageRequest backupCursorPageRequest) {

        // 1. pageable 생성
        Pageable pageable;
        if(backupCursorPageRequest.sortDirection().equals("ASC")){
            pageable = PageRequest.of(0, backupCursorPageRequest.size()+1, Sort.by(ASC, backupCursorPageRequest.sortField()));
        }
        else {
            pageable = PageRequest.of(0, backupCursorPageRequest.size()+1, Sort.by(DESC, backupCursorPageRequest.sortField()));
        }


        // 2. size + 1개 조회로 hasNext 판단
        String worker = null;
        if (backupCursorPageRequest.worker() != null && !backupCursorPageRequest.worker().isBlank()) {
            worker = "%" + backupCursorPageRequest.worker() + "%";
        }
        List<BackupDto> content;
        if(backupCursorPageRequest.cursor()==null){
            content = backupRepository.findAllWithoutCursor(
                    worker,
                    backupCursorPageRequest.status(),
                    backupCursorPageRequest.startedAtFrom(),
                    backupCursorPageRequest.startedAtTo(),
                    pageable
                ).stream()
                .map(backupMapper::toDto)
                .collect(Collectors.toList());
        }
        else{
            content = backupRepository.findAllWithCursor(
                    worker,
                    backupCursorPageRequest.status(),
                    backupCursorPageRequest.startedAtFrom(),
                    backupCursorPageRequest.startedAtTo(),
                    Instant.parse(backupCursorPageRequest.cursor()),
                    backupCursorPageRequest.idAfter(),
                    pageable
                ).stream()
                .map(backupMapper::toDto)
                .collect(Collectors.toList());
        }

        boolean hasNext = content.size() > backupCursorPageRequest.size();

        //3. nextCursor 찾기
        String nextCursor = content
            .stream()
            .reduce((first, second) -> second)
            .map(BackupDto::startedAt)
            .map(Instant::toString)
            .orElse(null);

        // 4. 현재 페이지의 마지막 요소 ID 찾기
        Long nextIdAfter = content
            .stream()
            .reduce((first, second) -> second)
            .map(BackupDto::id)
            .orElse(null);

        // 5. size 찾기
        int size = content.size()-1;

        // 6. totalElements 찾기
        Long totalElements = backupRepository.count();

        // 7. 마지막 행 제거
        content.remove(content.size() - 1);

        return new CursorPageResponseBackupDto(content,nextCursor,nextIdAfter,size,totalElements,hasNext);
    }

    /**
     * 지정된 상태의 가장 최근 백업 정보를 조회합니다.
     * 상태를 지정하지 않으면 성공적으로 완료된(COMPLETED) 백업을 반환합니다.
     * @param status 지정한 상태
     * @return 지정한 상태의 가장 최신 백업 정보
     */
    @Override
    public BackupDto getLatest(StatusType status) {
        return backupRepository
            .findAll()
            .stream()
            .filter(backup -> backup.getStatus().equals(status))
            .sorted(Comparator.comparing(Backup::getEndedAt).reversed())
            .limit(1)
            .findFirst()
            .map(backupMapper::toDto)
            .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 데이터 백업 필요 여부 확인후 필요시 백업이력 등록하는 메서드
     * 가장 최근 완료된 배치 작업 시간 이후 직원 데이터가 변경된 경우에 데이터 백업이 필요한 것으로 간주합니다.
     * 백업이 필요 없다면 건너뜀 상태로 배치 이력을 저장하고 프로세스를 종료합니다.
     * @param request 데이터 백업요청 Dto
     * @return 만들어진 DataBackup Dto 반환
     */
    @Override
    @Transactional
    public BackupDto create(HttpServletRequest request){

        // STEP 전 준비(worker ip 구하기, startedAt 설정)
        String worker = getIpAddress(request);
        Instant now = Instant.now();

        // STEP 1: 시작전 데이터 생성및 저장
        Backup backup = new Backup(worker, now,null, StatusType.IN_PROGRESS);
        backup = backupRepository.save(backup);

        // STEP 2: 데이터 백업 필요 여부 확인
        if(isBackupRequired()){
            // STEP 3: 백업 작업 (OOM 방지를 위한 스트리밍 방식)
            String name = "employee_backup_temp_name";
            String type = "employee_backup_temp_type";
            Long filesize = (long) 0;
            File file= new File(name,type,filesize); // CSV 파일 생성
            file = fileRepository.save(file);
            try {
                // 스트리밍 방식으로 직원 데이터 백업
                try (OutputStream os = fileStorage.put(file.getId(), ".csv");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {

                    writer.write("ID,직원번호,이름,이메일,부서,직급,입사일,상태\n");

                    int page = 0;
                    int size = 100;
                    Page<Employee> employeePage;

                    do {
                        employeePage = employeeRepository.findAll(PageRequest.of(page, size));
                        for (Employee emp : employeePage.getContent()) {
                            writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                                emp.getId(), emp.getEmployeeNumber(),emp.getName(), emp.getEmail(),emp.getDepartment().getName(), emp.getPosition(),emp.getHireDate(),emp.getStatus()));
                        }
                        page++;
                    } while (!employeePage.isLast());
                }

                // STEP 4-1: 성공 처리
                name = "employee_backup_"+file.getId()+"_"+now.atZone(TimeZone.getDefault().toZoneId());
                type = "text/csv";
                Path filePath = Paths.get(root + file.getId() + ".csv");
                filesize = Files.size(filePath);
                file.update(name,type,filesize);
                file = fileRepository.save(file);


                backup.update(Instant.now(), StatusType.COMPLETED, file);
                backup = backupRepository.save(backup);
                return backupMapper.toDto(backup);

            } catch (Exception e) {
                // STEP 4-2: 실패 처리
                if (file != null) {
                    fileRepository.delete(file);
                }
                // 에러 로그 파일 저장
                File logFile = new File("backup_failed_log_temp", "text/plain", 0L);
                logFile = fileRepository.save(logFile);

                try (OutputStream logOs = fileStorage.put(logFile.getId(), ".log");
                    BufferedWriter logWriter = new BufferedWriter(new OutputStreamWriter(logOs))) {
                    logWriter.write("백업 실패 사유:\n");
                    logWriter.write(e.getMessage());
                } catch (IOException ioException) {
                    throw new RuntimeException(ioException);
                }

                name = "backup_failed_log_"+logFile.getId()+"_"+now.atZone(TimeZone.getDefault().toZoneId());
                type = "text/plain";
                Path filePath = Paths.get(root + logFile.getId() + ".csv");
                try {
                    filesize = Files.size(filePath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                logFile.update(name,type,filesize);
                logFile = fileRepository.save(logFile);

                backup.update(Instant.now(), StatusType.FAILED, logFile);
                backup = backupRepository.save(backup);
                return backupMapper.toDto(backup);
            }

        }
        else{
            backup.update(Instant.now(), StatusType.SKIPPED, null);
            backup = backupRepository.save(backup);
            return backupMapper.toDto(backup);
        }
    }

    /**
     * 데이터 백업 필요 여부를 판단하는 메서드
     * 가장 최근 완료된 배치 작업 시간 이후 직원 데이터가 변경된 경우에 데이터 백업이 필요한 것으로 간주합니다.
     * 백업이 필요 없다면 건너뜀 상태로 배치 이력을 저장하고 프로세스를 종료합니다.
     * @return 백업이 필요하면 true, 필요 없다면 false 반환
     */
    private Boolean isBackupRequired(){
        Instant lastEndedAt = backupRepository
            .findAllByWorker("system")
            .stream()
            .sorted(Comparator.comparing(Backup::getEndedAt).reversed())
            .map(Backup::getEndedAt)
            .limit(1)
            .findFirst()
            .orElse(Instant.EPOCH);

        Instant lastUpdatedAt=changeLogRepository
            .findAll()
            .stream()
            .sorted(Comparator.comparing(ChangeLog::getUpdatedAt).reversed())
            .map(ChangeLog::getUpdatedAt)
            .limit(1)
            .findFirst()
            .orElse(Instant.EPOCH);

        if (lastUpdatedAt.isAfter(lastEndedAt)||lastUpdatedAt.equals(lastEndedAt)) {
            return true;
        }
        else {
            return false;
        }

    }


    /**
     * 백업 요청자의 Ip를 반환하는 메서드
     * @param request 요청자의 httpServletRequset
     * @return Ip주소
     */
    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded=For");
        if (ipAddress != null && !ipAddress.isBlank()) {
            return ipAddress.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public BackupDto executeBackup() {
        String worker = "system";
        Instant startedAt = Instant.now();

        // 1. 백업 히스토리 생성
        Backup backup = new Backup(worker, startedAt,null, StatusType.IN_PROGRESS);
        backup = backupRepository.save(backup);

        String name = "employee_backup_temp_name";
        String type = "employee_backup_temp_type";
        Long filesize = (long) 0;
        File file= new File(name,type,filesize); // CSV 파일 생성
        file = fileRepository.save(file);
        try {
            // 스트리밍 방식으로 직원 데이터 백업
            try (OutputStream os = fileStorage.put(file.getId(), ".csv");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {

                writer.write("ID,직원번호,이름,이메일,부서,직급,입사일,상태\n");

                int page = 0;
                int size = 100;
                Page<Employee> employeePage;

                do {
                    employeePage = employeeRepository.findAll(PageRequest.of(page, size));
                    for (Employee emp : employeePage.getContent()) {
                        writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                            emp.getId(), emp.getEmployeeNumber(),emp.getName(), emp.getEmail(),emp.getDepartment().getName(), emp.getPosition(),emp.getHireDate(),emp.getStatus()));
                    }
                    page++;
                } while (!employeePage.isLast());
            }

            // STEP 4-1: 성공 처리
            name = "employee_backup_"+file.getId()+"_"+startedAt.atZone(TimeZone.getDefault().toZoneId());
            type = "text/csv";
            Path filePath = Paths.get(root + file.getId() + ".csv");
            filesize = Files.size(filePath);
            file.update(name,type,filesize);
            file = fileRepository.save(file);


            backup.update(Instant.now(), StatusType.COMPLETED, file);
            backup = backupRepository.save(backup);
            return backupMapper.toDto(backup);

        } catch (Exception e) {
            // STEP 4-2: 실패 처리
            if (file != null) {
                fileRepository.delete(file);
            }
            // 에러 로그 파일 저장
            File logFile = new File("backup_failed_log_temp", "text/plain", 0L);
            logFile = fileRepository.save(logFile);

            try (OutputStream logOs = fileStorage.put(logFile.getId(), ".log");
                BufferedWriter logWriter = new BufferedWriter(new OutputStreamWriter(logOs))) {
                logWriter.write("백업 실패 사유:\n");
                logWriter.write(e.getMessage());
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }

            name = "backup_failed_log_"+logFile.getId()+"_"+startedAt.atZone(TimeZone.getDefault().toZoneId());
            type = "text/plain";
            Path filePath = Paths.get(root + logFile.getId() + ".csv");
            try {
                filesize = Files.size(filePath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            logFile.update(name,type,filesize);
            logFile = fileRepository.save(logFile);

            backup.update(Instant.now(), StatusType.FAILED, logFile);
            backup = backupRepository.save(backup);
            return backupMapper.toDto(backup);
        }


    }
}
