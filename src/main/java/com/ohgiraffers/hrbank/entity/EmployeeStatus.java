package com.ohgiraffers.hrbank.entity;

public enum EmployeeStatus {
    ACTIVE("재직중"),
    ON_LEAVE("휴직중"),
    RESIGNED("퇴사");

    private final String statusName;

    EmployeeStatus(String statusName) {
        this.statusName = statusName;
    }
}
