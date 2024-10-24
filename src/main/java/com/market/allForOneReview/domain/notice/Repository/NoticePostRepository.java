package com.market.allForOneReview.domain.notice.Repository;

import com.market.allForOneReview.domain.notice.Notice.NoticePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticePostRepository extends JpaRepository<NoticePost, Long>{
    // Board의 boardType에 따라 NoticePost를 찾는 메소드
    Page<NoticePost> findByBoard_BoardType(String boardType, Pageable pageable);
}
