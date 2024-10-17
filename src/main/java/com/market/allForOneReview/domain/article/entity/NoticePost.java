package com.market.allForOneReview.domain.article.entity;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class NoticePost extends BaseEntity {

    @Column
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SiteUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Board board;

    private Integer views;

    @OneToMany(mappedBy = "noticePost", cascade = CascadeType.REMOVE)
    private List<NoticeComment> comments;

}
