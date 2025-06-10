package com.ohgiraffers.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "backup_histories")
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "worker")
    private String worker;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "status")
    private StatusType status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;


    public Backup(String worker, Instant startedAt, Instant endedAt ,StatusType status) {
        this.worker = worker;
        this.startedAt = startedAt;
        this.status = status;
        this.endedAt = endedAt;
    }

    public void update(Instant endedAt, StatusType status, File file) {
        boolean anyValueUpdated = false;
        if (endedAt != null && !endedAt.equals(this.endedAt)) {
            this.endedAt = endedAt;
            anyValueUpdated = true;
        }
        if (status != null && !status.equals(this.status)) {
            this.status = status;
            anyValueUpdated = true;
        }
        if (file != null && !file.equals(this.file)) {
            this.file = file;
            anyValueUpdated = true;
        }

    }

    @Override
    public String toString() {
        return "DataBackup{" +
            "id=" + id +
            ", worker='" + worker + '\'' +
            ", startedAt=" + startedAt +
            ", endedAt=" + endedAt +
            ", status=" + status +
            ", file=" + file +
            '}';
    }
}
