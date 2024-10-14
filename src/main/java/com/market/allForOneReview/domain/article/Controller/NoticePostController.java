package com.market.allForOneReview.domain.article.Controller;

import com.market.allForOneReview.domain.article.Service.NoticePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NoticePostController {
    private final NoticePostService noticePostService;
}
