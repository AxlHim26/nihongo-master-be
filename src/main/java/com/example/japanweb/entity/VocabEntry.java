package com.example.japanweb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vocab_entries", indexes = {
        @Index(name = "idx_vocab_entries_course_term", columnList = "course_id, term"),
        @Index(name = "idx_vocab_entries_level", columnList = "level")
})
public class VocabEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private VocabCourse course;

    @Column(nullable = false, length = 100)
    private String term;

    @Column(nullable = false, length = 200)
    private String reading;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaning;

    @Column(columnDefinition = "TEXT")
    private String example;

    @Column(length = 10)
    private String level;

    @Column(name = "random_key", nullable = false, insertable = false, updatable = false)
    private Double randomKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
