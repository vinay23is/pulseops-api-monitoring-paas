package dev.pulseops.entity;

import dev.pulseops.entity.enums.MonitorMethod;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "monitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MonitorMethod method = MonitorMethod.GET;

    @Column(nullable = false)
    @Builder.Default
    private Integer expectedStatusCode = 200;

    @Column(nullable = false)
    @Builder.Default
    private Integer intervalSeconds = 60;

    @Column(nullable = false)
    @Builder.Default
    private Integer timeoutSeconds = 10;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    private Instant lastCheckedAt;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    @OneToMany(mappedBy = "monitor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CheckResult> checkResults;

    @OneToMany(mappedBy = "monitor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Incident> incidents;
}
