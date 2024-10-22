package com.market.allForOneReview.domain.notice;

import com.market.allForOneReview.DataNotFoundException;
import com.market.allForOneReview.domain.article.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticePostService {
    private final NoticePostRepository noticePostRepository;

    //    페이징
    public Page<Review> getReviewsByCategoryAndPage(String boardType, int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이징 처리
        return noticePostRepository.findByBoard_BoardType(boardType, pageable);
    }
}
