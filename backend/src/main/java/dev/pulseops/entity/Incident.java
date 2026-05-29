package dev.pulseops.entity;

import dev.pulseops.entity.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "incidents", indexes = {
    @Index(name = "idx_incident_monitor_id", columnList = "monitor_id"),
    @Index(name = "idx_incident_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.OPEN;

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant startedAt = Instant.now();

    private Instant resolvedAt;

    private Integer failureCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Monitor monitor;
}
