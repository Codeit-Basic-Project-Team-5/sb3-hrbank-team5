package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface ChangeLogService {

    Long registerChangeLog(ChangeLogRequest dto, HttpServletRequest request);

    String getIpAddress(HttpServletRequest request);

}
