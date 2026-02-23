package com.example.japanweb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_sections", indexes = {
        @Index(name = "idx_course_sections_chapter_type_order", columnList = "chapter_id, section_type, section_order")
})
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseChapter chapter;

    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false, length = 20)
    private CourseSectionType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "section_order", nullable = false)
    @Builder.Default
    private Integer sectionOrder = 0;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("lessonOrder ASC")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CourseLesson> lessons = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
