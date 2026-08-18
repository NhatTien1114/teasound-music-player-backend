package com.teasound.teasound_api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private boolean isPublic = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties("songs")
    private Author author;

    @Column(nullable = true)
    private String videoUrl;

    @Column(nullable = false)
    private String audioUrl;

    @Column(name = "lyric", columnDefinition = "TEXT")
    private String lyric;

    @Column(nullable = false)
    private String thumbnailUrl;
    private String duration;
    private Long likeCount = 0L;
    private Long viewCount = 0L;

    public enum Type {
        POP,
        ROCK,
        HIPHOP,
        RNB,
        EDM,
        JAZZ,
        CLASSICAL,
        LOFI,
        KPOP,
        VPOP,
        ACOUSTIC,
        INDIE,
        REMIX,
        OTHER
    }
}
