package com.market.allForOneReview.domain.notice.Comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<NoticeComment,Long> {
}
