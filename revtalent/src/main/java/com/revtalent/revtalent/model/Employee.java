package com.revtalent.revtalent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_employee_code", columnNames = "employee_code"),
                @UniqueConstraint(name = "uq_employee_user", columnNames = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "accountNonExpired",
            "accountNonLocked", "credentialsNonExpired", "enabled",
            "authorities", "password", "passwordHash", "employee"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"employees", "head", "hibernateLazyInitializer"})
    private Department department;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(nullable = false)
    private String designation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    private String phone;

    private String address;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = Status.ACTIVE;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        ON_LEAVE
    }
}