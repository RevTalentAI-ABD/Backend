package com.revtalent.revtalent.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "department",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_department_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_employee_id",
            foreignKey = @ForeignKey(name = "fk_department_head"))
    private Employee head;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private java.util.List<Employee> employees;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

