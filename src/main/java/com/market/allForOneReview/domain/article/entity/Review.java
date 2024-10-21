package com.market.allForOneReview.domain.article.entity;

import com.market.allForOneReview.domain.answer.Answer;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentStory;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "username")
    private SiteUser user;

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;
}


