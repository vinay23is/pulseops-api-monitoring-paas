package dev.pulseops.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "check_results", indexes = {
    @Index(name = "idx_check_result_monitor_id", columnList = "monitor_id"),
    @Index(name = "idx_check_result_checked_at", columnList = "checkedAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer statusCode;

    private Long latencyMs;

    @Column(nullable = false)
    private Boolean success;

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant checkedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Monitor monitor;
}
