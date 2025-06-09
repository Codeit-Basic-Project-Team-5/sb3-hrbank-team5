package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface ChangeLogService {

    Long registerChangeLog(ChangeLogRequestDto dto, HttpServletRequest request);

    String getIpAddress(HttpServletRequest request);


}
