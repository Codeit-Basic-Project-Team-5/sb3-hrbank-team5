package com.ohgiraffers.hrbank.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "change_logs")
public class ChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //CREATED , UPDATED, DELETED
    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "employee_id", nullable = false)
    private int employeeId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(length = 50)
    private String memo;

    @OneToMany(
        mappedBy = "changeLog",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ChangeLogDiff> diffs = new ArrayList<>();
}
