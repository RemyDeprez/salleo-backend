package com.salleo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(nullable = false, length = 50)
    private String action;

    @ManyToOne
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @Column(columnDefinition = "jsonb")
    private String details;

    @Column(name = "created_at")
    private Instant createdAt;
}

