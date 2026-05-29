package dev.pulseops.entity;

import dev.pulseops.entity.enums.AlertStatus;
import dev.pulseops.entity.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alert_monitor_id", columnList = "monitor_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertStatus status = AlertStatus.PENDING;

    @Column(length = 2000)
    private String message;

    private String webhookUrl;

    private String failureReason;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Monitor monitor;
}
