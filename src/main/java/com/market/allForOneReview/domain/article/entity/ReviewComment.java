package com.market.allForOneReview.domain.article.entity;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ReviewComment extends BaseEntity {

    @Column(length=5000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser siteUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReviewPost reviewPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ReviewComment parentAnswer;  // 부모 댓글 (답글일 경우)

    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.ALL)
    private List<ReviewComment> childAnswers = new ArrayList<>();  // 자식 댓글들 (답글들)

}
