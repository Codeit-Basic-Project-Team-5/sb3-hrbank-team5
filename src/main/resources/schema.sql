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
    description varchar(150),
    established_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS backup_history
(
    id           BIGSERIAL PRIMARY KEY,
    started_at   timestamp  NOT NULL,
    end_at   timestamp,
    status    VARCHAR(10) NOT NULL
        CHECK (status IN ('진행중','완료','실패','건너뜀')),
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
    status          VARCHAR(10),
    memo            VARCHAR(255),
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
    updated_at   TIMESTAMP NOT NULL,
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
    created_at     TIMESTAMP NOT NULL
);