package com.market.allForOneReview.domain.user.entity;

import com.market.allForOneReview.domain.article.entity.NoticePost;
import com.market.allForOneReview.domain.article.entity.ReviewPost;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SiteUser extends BaseEntity {
    @Column(length = 100, nullable = false, unique = true)
    private String userId;

    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column
    private int authority;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ReviewPost> reviewPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<NoticePost> noticePosts;
}