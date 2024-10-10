package com.market.allForOneReview.domain.article.entity;

import jakarta.persistence.*;

import java.util.List;

public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_type", nullable = false, unique = true)
    private String boardType;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<NoticePost> posts;
}
