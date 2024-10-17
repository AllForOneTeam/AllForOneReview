package com.market.allForOneReview.domain.article.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "review_category")
@Getter
@Setter
public class ReviewCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;


    @Column
    private String subCategory;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ReviewPost> posts;
}
