package com.market.allForOneReview.domain.notice.Comment;

import com.market.allForOneReview.domain.notice.Notice.NoticePost;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void create(NoticePost noticePost, String content, SiteUser author){
        NoticeComment noticeComment = new NoticeComment();
        noticeComment.setContent(content);
        noticeComment.setNoticePost(noticePost);
        noticeComment.setCreateDate(LocalDateTime.now());
        noticeComment.setAuthor(author);
        this.commentRepository.save(noticeComment);
    }
}
