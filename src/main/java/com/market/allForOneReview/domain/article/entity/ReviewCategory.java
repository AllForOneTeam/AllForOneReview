package com.market.allForOneReview.domain.article.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "review_category")
public class ReviewCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ReviewPost> posts;
}
