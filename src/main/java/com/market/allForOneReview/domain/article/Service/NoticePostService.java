package com.market.allForOneReview.domain.article.Service;

import com.market.allForOneReview.domain.article.Repository.NoticePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticePostService {
    private final NoticePostRepository noticePostRepository;
}
