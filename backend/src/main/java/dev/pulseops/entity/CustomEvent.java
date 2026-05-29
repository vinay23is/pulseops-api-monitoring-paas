package dev.pulseops.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "custom_events", indexes = {
    @Index(name = "idx_custom_event_project_id", columnList = "project_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ApiKey apiKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;
}
