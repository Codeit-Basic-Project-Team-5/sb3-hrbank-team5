-- 1. 테이블 삭제
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS change_log_diff CASCADE;
DROP TABLE IF EXISTS change_log CASCADE;
DROP TABLE IF EXISTS backup_histories CASCADE;
DROP TABLE IF EXISTS files CASCADE;


-- 2. 테이블 생성
CREATE TABLE files (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    size BIGINT NOT NULL
);

CREATE TABLE departments
(
    id BIGSERIAL PRIMARY KEY ,
    name varchar(50) NOT NULL ,
    description varchar(150) NOT NULL,
    established_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS backup_histories
(
    id           BIGSERIAL PRIMARY KEY,
    started_at   TIMESTAMPTZ  NOT NULL,
    ended_at   TIMESTAMPTZ,
    status    VARCHAR(20) NOT NULL
        CHECK (status IN ('IN_PROGRESS','COMPLETED','FAILED','SKIPPED')),
    worker VARCHAR(50) NOT NULL ,
    file_id BIGINT
        REFERENCES files (id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS employees
(
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(50)             NOT NULL,
    email           VARCHAR(255)            NOT NULL UNIQUE,
    employee_number VARCHAR(50)             NOT NULL,
    department_id   BIGINT                  NOT NULL
        REFERENCES departments (id)
            ON DELETE RESTRICT,
    position        VARCHAR(50)             NOT NULL,
    hire_date       DATE                    NOT NULL,
    status          VARCHAR(10)             NOT NULL
        CHECK (status IN ('ACTIVE','ON_LEAVE','RESIGNED')),
    profile_image_id    BIGINT
        REFERENCES files (id)
            ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS change_logs
(
    id           BIGSERIAL PRIMARY KEY,
    type         VARCHAR(20) NOT NULL,
    employee_id  INT NOT NULL
        REFERENCES employees(id)
            ON DELETE RESTRICT,
    ip_address   VARCHAR(50),
    updated_at   TIMESTAMPTZ NOT NULL,
    memo         VARCHAR(500)
);

CREATE TABLE change_log_diff (
    id             BIGSERIAL PRIMARY KEY,
    change_log_id  BIGINT NOT NULL
        REFERENCES change_logs(id)
            ON DELETE CASCADE,
    field_name     VARCHAR(100) NOT NULL,
    old_value      VARCHAR(500),
    new_value      VARCHAR(500),
    created_at     TIMESTAMPTZ NOT NULL
);

-- 3. 더미데이터 생성

INSERT INTO departments(name, description, established_date)
VALUES ('인사팀','인사팀입니다','1999-11-24');


INSERT INTO employees (id, name, email, employee_number, department_id, position, hire_date, status)
VALUES (1,'너굴', 'tom.nook@example.com', 'EMP-2024-001', 1, 'CEO', '2024-03-15', 'ACTIVE'),
       (2,'여울', 'isabelle@example.com', 'EMP-2024-002', 1, '비서실장', '2024-04-01', 'ACTIVE'),
       (3,'부엉', 'blathers@example.com', 'EMP-2024-003', 1, '선임 연구원', '2024-03-20', 'ACTIVE'),
       (4,'콩돌이', 'timmy@example.com', 'EMP-2024-004', 1, '주니어 개발자', '2024-05-01', 'ON_LEAVE'),
       (5,'밤돌이', 'tommy@example.com', 'EMP-2024-005', 1, '주니어 개발자', '2024-05-01', 'ACTIVE'),
       (6,'도루묵씨', 'resetti@example.com', 'EMP-2024-006', 1, 'QA 매니저', '2024-02-10', 'RESIGNED'),
       (7,'패트릭', 'flick@example.com', 'EMP-2024-007', 1, 'UI/UX 디자이너', '2024-04-20', 'ACTIVE'),
       (8,'그레이스', 'grace.ac@example.com', 'EMP-2024-008', 1, '마케팅 전문가', '2024-01-25', 'ACTIVE'),
       (9,'고순이', 'sable.ac@example.com', 'EMP-2024-009', 1, '경영 지원', '2024-03-05', 'ACTIVE'),
       (10,'고옥이', 'mabel.ac@example.com', 'EMP-2024-010', 1, '재무 담당자', '2024-03-05', 'ACTIVE'),
       (11,'갑돌이', 'kappn@example.com', 'EMP-2024-011', 1, '운송팀장', '2024-02-14', 'ACTIVE'),
       (12,'늘봉', 'leif@example.com', 'EMP-2024-012', 1, '환경 관리 전문가', '2024-04-10', 'ACTIVE'),
       (13,'마르셀', 'marcel@example.com', 'EMP-2024-013', 1, '컨텐츠 기획자', '2024-03-28', 'ON_LEAVE'),
       (14,'미애', 'midge@example.com', 'EMP-2024-014', 1, '프론트엔드 개발자', '2024-04-05', 'ACTIVE'),
       (15,'미자', 'mitzi@example.com', 'EMP-2024-015', 1, '백엔드 개발자', '2024-04-12', 'ACTIVE'),
       (16,'분홍', 'puddles@example.com', 'EMP-2024-016', 1, '영업 담당자', '2024-03-01', 'ACTIVE'),
       (17,'빙티', 'dobie@example.com', 'EMP-2024-017', 1, '데이터 분석가', '2024-02-20', 'ACTIVE'),
       (18,'사라', 'sara@example.com', 'EMP-2024-018', 1, 'HR 매니저', '2024-01-15', 'RESIGNED'),
       (19,'산드라', 'sandra@example.com', 'EMP-2024-019', 1, '클라우드 엔지니어', '2024-05-10', 'ACTIVE'),
       (20,'아델레이드', 'adelaide@example.com', 'EMP-2024-020', 1, 'IT 지원', '2024-04-25', 'ACTIVE');


INSERT INTO backup_histories (worker, started_at, ended_at, status, file_id)
VALUES ( '119.67.218.113', '2025-04-23T05:25:42.830071Z', '2025-04-23T05:25:42.844961Z', 'COMPLETED', NULL),
       ('180.65.189.133', '2025-04-22T13:03:27.806745Z', '2025-04-22T13:03:27.820251Z', 'COMPLETED', NULL),
       ( '180.65.189.133', '2025-04-22T13:02:50.596310Z', '2025-04-22T13:02:50.607851Z', 'COMPLETED', NULL),
       ( '180.65.189.133', '2025-04-22T05:12:37.220998Z', '2025-04-22T05:12:37.234208Z', 'COMPLETED', NULL),
       ( '220.77.3.119', '2025-04-22T01:25:48.691646Z', '2025-04-22T01:25:48.705215Z', 'COMPLETED', NULL),
       ( '121.144.72.75', '2025-04-21T14:46:41.691229Z', '2025-04-21T14:46:41.698863Z', 'SKIPPED', NULL),
       ( '220.77.3.119', '2025-04-21T09:40:57.262644Z', '2025-04-21T09:40:57.270751Z', 'SKIPPED', NULL),
       ( '121.144.72.75', '2025-04-21T08:27:30.702880Z', '2025-04-21T08:27:30.720248Z', 'COMPLETED', NULL),
       ( '218.52.157.62', '2025-04-21T06:19:07.977670Z', '2025-04-21T06:19:07.982192Z', 'SKIPPED', NULL),
       ( '218.52.157.62', '2025-04-21T06:19:07.305325Z', '2025-04-21T06:19:07.310142Z', 'SKIPPED', NULL),
       ( '218.52.157.62', '2025-04-21T06:19:06.499728Z', '2025-04-21T06:19:06.513077Z', 'COMPLETED', NULL),
       ('218.52.157.62', '2025-04-21T06:19:05.592947Z', '2025-04-21T06:19:05.597200Z', 'SKIPPED', NULL),
       ( '218.52.157.62', '2025-04-21T06:19:04.801466Z', '2025-04-21T06:19:04.813917Z', 'COMPLETED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:04.886186Z', '2025-04-21T05:18:04.891359Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:04.460092Z', '2025-04-21T05:18:04.465421Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:04.214379Z', '2025-04-21T05:18:04.219375Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:03.883813Z', '2025-04-21T05:18:03.889105Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:03.556182Z', '2025-04-21T05:18:03.561763Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:03.134408Z', '2025-04-21T05:18:03.139746Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:02.514911Z', '2025-04-21T05:18:02.520163Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:01.878758Z', '2025-04-21T05:18:01.884151Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:01.176155Z', '2025-04-21T05:18:01.181432Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:18:00.527216Z', '2025-04-21T05:18:00.534405Z', 'SKIPPED', NULL),
       ('14.63.67.157', '2025-04-21T05:17:59.526701Z', '2025-04-21T05:17:59.531705Z', 'SKIPPED', NULL),
       ( '14.63.67.157', '2025-04-21T05:16:37.513313Z', '2025-04-21T05:16:37.518667Z', 'SKIPPED', NULL),
       ( '175.193.75.221', '2025-04-21T05:16:31.418050Z', '2025-04-21T05:16:31.423035Z', 'SKIPPED', NULL),
       ('1.247.87.165', '2025-04-21T05:16:00.687720Z', '2025-04-21T05:16:00.708240Z', 'COMPLETED', NULL),
       ('1.247.87.165', '2025-04-21T05:15:16.319124Z', '2025-04-21T05:15:16.324234Z', 'SKIPPED', NULL),
       ('1.247.87.165', '2025-04-21T05:15:02.754274Z', '2025-04-21T05:15:02.774752Z', 'COMPLETED', NULL),
       ('175.193.75.221', '2025-04-21T02:22:27.914648Z', '2025-04-21T02:22:27.919823Z', 'SKIPPED', NULL),
       ('175.193.75.221', '2025-04-21T02:22:27.222777Z', '2025-04-21T02:22:27.227864Z', 'SKIPPED', NULL),
        ( '175.193.75.221', '2025-04-21T02:22:23.655094Z', '2025-04-21T02:22:23.660195Z','SKIPPED', NULL),
       ('121.144.72.75', '2025-04-21T02:20:56.757538Z', '2025-04-21T02:20:56.763057Z','SKIPPED', NULL),
       ('121.144.72.75', '2025-04-21T02:20:36.504543Z', '2025-04-21T02:20:36.519658Z','COMPLETED', NULL),
       ('220.77.3.119', '2025-04-21T01:37:40.518955Z', '2025-04-21T01:37:40.541564Z','COMPLETED', NULL),
       ('180.65.189.133', '2025-04-20T15:31:07.499496Z', '2025-04-20T15:31:07.505811Z','SKIPPED', NULL),
       ('180.65.189.133', '2025-04-20T15:29:31.969907Z', '2025-04-20T15:29:31.993032Z','COMPLETED', NULL),
       ('180.65.189.133', '2025-04-19T10:00:18.100449Z', '2025-04-19T10:00:18.107217Z','SKIPPED', NULL),
       ( '180.65.189.133', '2025-04-19T10:00:17.220859Z', '2025-04-19T10:00:17.227610Z','SKIPPED', NULL),
       ('180.65.189.133', '2025-04-19T10:00:17.040586Z', '2025-04-19T10:00:17.047433Z','SKIPPED', NULL),
       ('180.65.189.133', '2025-04-19T10:00:16.678209Z', '2025-04-19T10:00:16.684617Z','SKIPPED', NULL),
       ('180.65.189.133', '2025-04-19T10:00:14.277656Z', '2025-04-19T10:00:14.305872Z','COMPLETED', NULL),
       ('175.202.152.206', '2025-04-18T08:38:23.726513Z', '2025-04-18T08:38:23.734518Z','SKIPPED', NULL),
       ('112.169.106.184', '2025-04-18T07:20:27.279941Z', '2025-04-18T07:20:27.336740Z','SKIPPED', NULL),
       ('system', '2025-04-17T05:45:57.873937Z', '2025-04-17T05:45:57.919445Z', 'SKIPPED', NULL),
       ('system', '2025-04-17T05:45:57.739259Z', '2025-04-17T05:45:57.863262Z', 'COMPLETED', NULL),
       ('system', '2025-04-17T05:45:57.444328Z', '2025-04-17T05:45:57.554389Z', 'COMPLETED', NULL),
       ('system', '2025-04-17T05:45:57.075433Z', '2025-04-17T05:45:57.157895Z', 'COMPLETED', NULL),
       ('system', '2025-04-17T05:45:56.687677Z', '2025-04-17T05:45:56.771990Z', 'COMPLETED', NULL);
