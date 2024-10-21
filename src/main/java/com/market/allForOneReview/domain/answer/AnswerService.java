package com.market.allForOneReview.domain.answer;

import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void create(Review review, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setReview(review);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
    }
}
