-- 1. 테이블 삭제
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS change_log_diff CASCADE;
DROP TABLE IF EXISTS change_log CASCADE;
DROP TABLE IF EXISTS backup_history CASCADE;
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
    status    VARCHAR(10) NOT NULL
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


INSERT INTO backup_histories (id, worker, started_at, ended_at, status, file_id)
VALUES (18, '175.193.75.221', '2025-04-21T02:22:23.655094Z', '2025-04-21T02:22:23.660195Z','SKIPPED', NULL),
       (17, '121.144.72.75', '2025-04-21T02:20:56.757538Z', '2025-04-21T02:20:56.763057Z','SKIPPED', NULL),
       (16, '121.144.72.75', '2025-04-21T02:20:36.504543Z', '2025-04-21T02:20:36.519658Z','COMPLETED', 8),
       (15, '220.77.3.119', '2025-04-21T01:37:40.518955Z', '2025-04-21T01:37:40.541564Z','COMPLETED', 7),
       (14, '180.65.189.133', '2025-04-20T15:31:07.499496Z', '2025-04-20T15:31:07.505811Z','SKIPPED', NULL),
       (13, '180.65.189.133', '2025-04-20T15:29:31.969907Z', '2025-04-20T15:29:31.993032Z','COMPLETED', 6),
       (12, '180.65.189.133', '2025-04-19T10:00:18.100449Z', '2025-04-19T10:00:18.107217Z','SKIPPED', NULL),
       (11, '180.65.189.133', '2025-04-19T10:00:17.220859Z', '2025-04-19T10:00:17.227610Z','SKIPPED', NULL),
       (10, '180.65.189.133', '2025-04-19T10:00:17.040586Z', '2025-04-19T10:00:17.047433Z','SKIPPED', NULL),
       (9, '180.65.189.133', '2025-04-19T10:00:16.678209Z', '2025-04-19T10:00:16.684617Z','SKIPPED', NULL),
       (8, '180.65.189.133', '2025-04-19T10:00:14.277656Z', '2025-04-19T10:00:14.305872Z','COMPLETED', 5),
       (7, '175.202.152.206', '2025-04-18T08:38:23.726513Z', '2025-04-18T08:38:23.734518Z','SKIPPED', NULL),
       (6, '112.169.106.184', '2025-04-18T07:20:27.279941Z', '2025-04-18T07:20:27.336740Z','SKIPPED', NULL),
       (5, 'system', '2025-04-17T05:45:57.873937Z', '2025-04-17T05:45:57.919445Z', 'SKIPPED', NULL),
       (4, 'system', '2025-04-17T05:45:57.739259Z', '2025-04-17T05:45:57.863262Z', 'COMPLETED', 4),
       (3, 'system', '2025-04-17T05:45:57.444328Z', '2025-04-17T05:45:57.554389Z', 'COMPLETED', 3),
       (2, 'system', '2025-04-17T05:45:57.075433Z', '2025-04-17T05:45:57.157895Z', 'COMPLETED', 2),
       (1, 'system', '2025-04-17T05:45:56.687677Z', '2025-04-17T05:45:56.771990Z', 'COMPLETED', 1);

