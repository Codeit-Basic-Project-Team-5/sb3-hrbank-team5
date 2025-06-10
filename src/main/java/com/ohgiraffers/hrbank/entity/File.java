package com.ohgiraffers.hrbank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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