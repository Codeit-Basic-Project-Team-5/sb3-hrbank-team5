package com.ohgiraffers.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class File {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Column(name = "size", nullable = false)
    private Long size;

    public File(String name, String type, Long size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public void update(String name, String type, Long size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    @Override
    public String toString() {
        return "File{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", size=" + size +
            '}';
    }
}