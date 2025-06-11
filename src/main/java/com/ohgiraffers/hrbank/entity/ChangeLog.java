package com.ohgiraffers.hrbank.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
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
    private Long employeeId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    public void setDiffs(List<ChangeLogDiff> diffs) {
        this.diffs.clear();
        if (diffs != null) {
            diffs.forEach(d -> {
                d.setChangeLog(this);
                this.diffs.add(d);
            });
        }
    }
}
