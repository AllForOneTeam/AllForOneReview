package com.market.allForOneReview.domain.notice.Comment;

import com.market.allForOneReview.domain.answer.Answer;
import com.market.allForOneReview.domain.notice.Notice.NoticePost;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public NoticeComment create(NoticePost noticePost, String content, SiteUser author){
        NoticeComment noticeComment = new NoticeComment();
        noticeComment.setContent(content);
        noticeComment.setNoticePost(noticePost);
        noticeComment.setCreateDate(LocalDateTime.now());
        noticeComment.setAuthor(author);
        this.commentRepository.save(noticeComment);
        return noticeComment;
    }

    public NoticeComment getComment(Long id) {
        Optional<NoticeComment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }
    public void modify(NoticeComment comment, String content) {
        comment.setContent(content);
        comment.setModifiedDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    public void delete(NoticeComment comment) {
        this.commentRepository.delete(comment);
    }
}
