package com.example.japanweb.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "grammar_examples")
public class GrammarExample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grammar_point_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GrammarPoint grammarPoint;

    @Column(name = "sentence_jp", nullable = false, columnDefinition = "TEXT")
    private String sentenceJp;

    @Column(name = "sentence_vn", nullable = false, columnDefinition = "TEXT")
    private String sentenceVn;

    @Column(name = "audio_url")
    private String audioUrl;
}
