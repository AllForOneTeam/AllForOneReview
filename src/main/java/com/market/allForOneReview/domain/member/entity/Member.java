package com.market.allForOneReview.domain.member.entity;

import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
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

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<ReviewPost> reviewPosts;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<NoticePost> noticePosts;
}