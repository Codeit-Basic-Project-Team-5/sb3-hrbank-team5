package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.entity.Department;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.mapper.EmployeeMapper;
import com.ohgiraffers.hrbank.repository.DepartmentRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.EmployeeService;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeDto create(EmployeeCreateRequest employeeCreateRequest) {
        Department department = departmentRepository.findDepartmentById(employeeCreateRequest.departmentId());

        String memo = employeeCreateRequest.memo();

        Employee employee = new Employee(
            employeeCreateRequest.name(),
            employeeCreateRequest.email(),
            "EMP",
            department,
            employeeCreateRequest.position(),
            employeeCreateRequest.hireDate()
        );
       employeeRepository.save(employee);

        return employeeMapper.toDto(employee);
    }

    @Override
    public void delete(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new NoSuchElementException("Employee with id " + employeeId + " not found");
        }

        employeeRepository.deleteById(employeeId);
    }

}
