package com.market.allForOneReview.domain.article.entity;

import com.market.allForOneReview.domain.member.entity.SiteUser;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor


public class ReviewPost extends BaseEntity {

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentStory;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(targetEntity = SiteUser.class, fetch = FetchType.LAZY)
    private SiteUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ReviewCategory category;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.REMOVE)
    private List<ReviewComment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Image> images;

    @Column
    private int views;

    @ElementCollection
    private List<String> tags; // 태그를 리스트로 저장

    @Column
    private int vote;
}


