package ru.practicum.ewm.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ToString.Exclude
    private Category category;

    @Column(name = "created_om", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column(name = "description", length = 7000)
    private String description;

    @Column(name = "event_date", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime eventDate;

    @Column(name = "lat", nullable = false)
    private float lat;

    @Column(name = "lon", nullable = false)
    private float lon;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "title", length = 120)
    private String title;

    @Column(name = "state", length = 16)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EventState state = EventState.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private User initiator;

    @Column(name = "published_on", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @CreationTimestamp
    private LocalDateTime publishedOn;
}

