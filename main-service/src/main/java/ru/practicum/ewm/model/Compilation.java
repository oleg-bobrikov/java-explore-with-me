package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "event_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @Builder.Default
    private Set<Event> events = new HashSet<>();

    @Column(name = "pinned", nullable = false)
    private boolean pinned;

    @Column(name = "title", nullable = false, length = 50)
    private String title;
}
