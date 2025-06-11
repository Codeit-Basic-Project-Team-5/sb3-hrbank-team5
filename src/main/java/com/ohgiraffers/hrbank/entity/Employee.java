package com.ohgiraffers.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "employee_number", nullable = false, length = 50)
    private String employeeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "position", nullable = false, length = 50)
    private String position;

    @CreatedDate
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private EmployeeStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private File profileImage;

    public Employee(String name, String email, String employeeNumber, Department department, String position, LocalDate hireDate, File profileImage) {
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.email = Objects.requireNonNull(email, "Email must not be null");
        this.employeeNumber = employeeNumber;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.status = EmployeeStatus.ACTIVE;
        this.profileImage = profileImage;
    }

    public void update(String newName, String newEmail, Department newDepartment, String newPosition, LocalDate newHireDate, EmployeeStatus newStatus, File newProfileImage) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newDepartment != null && !newDepartment.equals(this.department)) {
            this.department = newDepartment;
        }
        if (newPosition != null && !newPosition.equals(this.position)) {
            this.position = newPosition;
        }
        if (newHireDate != null && !newHireDate.equals(this.hireDate)) {
            this.hireDate = newHireDate;
        }
        if (newStatus != null && !newStatus.equals(this.status)) {
            this.status = newStatus;
        }
        if (newProfileImage != null) {
            this.profileImage = newProfileImage;
        }
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
