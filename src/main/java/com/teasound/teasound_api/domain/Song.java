package com.teasound.teasound_api.domain;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "songs")
@Getter
@Setter
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(nullable = true)
    private String videoUrl;

    @Column(nullable = false)
    private String audioUrl;

    @Column(nullable = false)
    private String thumbnailUrl;
    private String duration;
    private Long likeCount;
    private Long viewCount;

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
