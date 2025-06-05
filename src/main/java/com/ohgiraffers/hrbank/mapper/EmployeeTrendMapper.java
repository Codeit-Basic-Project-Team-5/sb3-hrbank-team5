package com.ohgiraffers.hrbank.mapper;

import com.ohgiraffers.hrbank.dto.data.EmployeeTrendDto;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTrendMapper {

    /**
     * employees 테이블 직원 수 연산 결과 -> EmployeeTrendDto
     *
     * @param dataList
     *  - Object[0]: 해당 날짜 (date)
     *  - Object[1]: 날짜 별 직원 수 (count)
     * @return EmployeeTrendDto
     */
    public static List<EmployeeTrendDto> toDtoList(List<Object[]> dataList) {
        List<EmployeeTrendDto> dtoList = new ArrayList<>();

        long previousCount = 0L;
        boolean firstDate = true;

        for (Object[] row : dataList) {
            LocalDate date = ((Date) row[0]).toLocalDate();
            long count = ((Number) row[1]).longValue();

            long changeValue;
            double changeRateValue;

            if (firstDate) {
                changeValue = 0L;
                changeRateValue = 0.0;
                firstDate = false;
            } else {
                changeValue = count - previousCount;
                if (previousCount == 0L) {
                    changeRateValue = 0.0;
                } else {
                    changeRateValue = (double) changeValue / previousCount * 100.0;
                }
            }

            EmployeeTrendDto dto = new EmployeeTrendDto(
                date,
                count,
                changeValue,
                changeRateValue
            );
            dtoList.add(dto);

            previousCount = count;
        }

        return dtoList;
    }
}