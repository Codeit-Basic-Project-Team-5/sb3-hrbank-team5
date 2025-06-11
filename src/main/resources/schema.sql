-- 1. 테이블 삭제
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS change_log_diff CASCADE;
DROP TABLE IF EXISTS change_logs CASCADE;
DROP TABLE IF EXISTS backup_histories CASCADE;
DROP TABLE IF EXISTS files CASCADE;


-- 2. 테이블 생성
CREATE TABLE files
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    size BIGINT      NOT NULL
);

CREATE TABLE departments
(
    id               BIGSERIAL PRIMARY KEY,
    name             varchar(50)  NOT NULL,
    description      varchar(150) NOT NULL,
    established_date DATE         NOT NULL
);

CREATE TABLE IF NOT EXISTS backup_histories
(
    id         BIGSERIAL PRIMARY KEY,
    started_at TIMESTAMPTZ NOT NULL,
    ended_at   TIMESTAMPTZ,
    status     VARCHAR(20) NOT NULL
        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED', 'SKIPPED')),
    worker     VARCHAR(50) NOT NULL,
    file_id    BIGINT
                           REFERENCES files (id)
                               ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS employees
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(50)  NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    employee_number  VARCHAR(50)  NOT NULL,
    department_id    BIGINT       NOT NULL
        REFERENCES departments (id)
            ON DELETE RESTRICT,
    position         VARCHAR(50)  NOT NULL,
    hire_date        DATE         NOT NULL,
    status           VARCHAR(10)  NOT NULL
        CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'RESIGNED')),
    profile_image_id BIGINT
                                  REFERENCES files (id)
                                      ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS change_logs
(
    id          BIGSERIAL PRIMARY KEY,
    type        VARCHAR(20) NOT NULL,
    employee_id BIGSERIAL   NOT NULL
        REFERENCES employees (id)
            ON DELETE RESTRICT,
    ip_address  VARCHAR(50),
    updated_at  TIMESTAMPTZ NOT NULL,
    memo        VARCHAR(500)
);

CREATE TABLE change_log_diff
(
    id            BIGSERIAL PRIMARY KEY,
    change_log_id BIGINT       NOT NULL
        REFERENCES change_logs (id)
            ON DELETE CASCADE,
    field_name    VARCHAR(100) NOT NULL,
    old_value     VARCHAR(500),
    new_value     VARCHAR(500),
    created_at    TIMESTAMPTZ  NOT NULL
);


