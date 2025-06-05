package com.ohgiraffers.hrbank.mapper;

import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDistributionMapper {

    /**
     * departments 테이블 연산 결과, 전체 직원 수 -> EmployeeDistributionDto
     *
     * @param dataList
     *  - Object[0] : 부서/직무명 (String)
     *  - Object[1] : 해당 부서/직무 별 직원 수 (long)
     * @param totalCount 전체 직원 수
     * @return EmployeeDistributionDto
     */
    public static List<EmployeeDistributionDto> toDtoList(List<Object[]> dataList, long totalCount) {
        List<EmployeeDistributionDto> dtoList = new ArrayList<>();

        // 직원 수가 0 이하인 경우, 0으로 나눗셈하지 않도록 방지
        if (totalCount <= 0) {
            for (Object[] row : dataList) {
                String deptName = (String) row[0];
                long count = ((Number) row[1]).longValue();
                dtoList.add(new EmployeeDistributionDto(
                    deptName,
                    count,
                    0.0
                ));
            }
            return dtoList;
        }

        for (Object[] row : dataList) {
            String deptName = (String) row[0];
            long count = ((Number) row[1]).longValue();
            double percentage = ((double) count / totalCount) * 100.0;

            EmployeeDistributionDto dto = new EmployeeDistributionDto(
                deptName,
                count,
                percentage
            );
            dtoList.add(dto);
        }

        return dtoList;
    }
}
