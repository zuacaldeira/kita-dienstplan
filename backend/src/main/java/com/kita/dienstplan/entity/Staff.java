package com.kita.dienstplan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a staff member
 */
@Entity
@Table(name = "staff")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "full_name", nullable = false, unique = true)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "employment_type", length = 50)
    private String employmentType; // full-time, part-time, intern

    @Column(name = "weekly_hours", precision = 5, scale = 2)
    private BigDecimal weeklyHours; // Weekly working hours

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(name = "is_praktikant", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPraktikant = false;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One staff member has many schedule entries
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleEntry> scheduleEntries = new ArrayList<>();

    // Helper methods
    public void addScheduleEntry(ScheduleEntry entry) {
        scheduleEntries.add(entry);
        entry.setStaff(this);
    }

    public void removeScheduleEntry(ScheduleEntry entry) {
        scheduleEntries.remove(entry);
        entry.setStaff(null);
    }
}
