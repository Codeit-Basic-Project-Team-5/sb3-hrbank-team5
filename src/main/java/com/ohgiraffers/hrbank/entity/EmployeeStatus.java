package com.ohgiraffers.hrbank.entity;

public enum EmployeeStatus {
    ACTIVE("재직중"),
    ON_LEAVE("휴직중"),
    RESIGNED("퇴사");

    private final String statusName;

    EmployeeStatus(String statusName) {
        this.statusName = statusName;
    }

    // 한국어 상태명을 반환하는 getter 메서드
    public String getStatusName() {
        return statusName;
    }
}
