package com.market.allForOneReview.domain.notice;

import com.market.allForOneReview.DataNotFoundException;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.notice.Notice.NoticePost;
import com.market.allForOneReview.domain.notice.Repository.BoardRepository;
import com.market.allForOneReview.domain.notice.Repository.NoticePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticePostService {
    private final NoticePostRepository noticePostRepository;
    private final BoardRepository boardRepository;

    //    페이징
    public Page<NoticePost> getNoticesByBoardTypeAndPage(String boardType, int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이징 처리
        return noticePostRepository.findByBoard_BoardType(boardType, pageable);
    }

    public NoticePost getNotice(Long id) {
        Optional<NoticePost> notice = this.noticePostRepository.findById(id);
        if (notice.isPresent()) {
            return notice.get();
        } else {
            throw new DataNotFoundException("notice not found");
        }
    }
    public void create(String title, String content, String boardType){

        Optional<Board> board = this.boardRepository.findByBoardType(boardType);
        NoticePost noticePost = new NoticePost();
        noticePost.setTitle(title);
        noticePost.setContent(content);
        noticePost.setCreateDate(LocalDateTime.now());
        noticePost.setBoard(board.get());
        this.noticePostRepository.save(noticePost);
    }
    public void modify(NoticePost notice, String title, String content){
        notice.setTitle(title);
        notice.setContent(content);
        notice.setModifiedDate(LocalDateTime.now());
        this.noticePostRepository.save(notice);
    }
    public void delete(NoticePost notice){
        this.noticePostRepository.delete(notice);
    }
}
