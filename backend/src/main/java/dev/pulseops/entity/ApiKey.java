package dev.pulseops.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "api_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String keyHash;

    @Column(nullable = false, unique = true)
    private String keyPrefix;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;
}
