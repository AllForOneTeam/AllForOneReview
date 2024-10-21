package com.market.allForOneReview.domain.article.Service;

import com.market.allForOneReview.DataNotFoundException;
import com.market.allForOneReview.domain.article.Repository.ReviewRepository;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getList() {
        return this.reviewRepository.findAll();
    }

    public Review getReview(Long id) {
        Optional<Review> review = this.reviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new DataNotFoundException("review not found");
        }
    }

    //    게시글 저장
    public void create(String title, String contentStory, String content, SiteUser user) {
        Review r = new Review();
        r.setTitle(title);
        r.setContentStory(contentStory);
        r.setContent(content);
        r.setCreateDate(LocalDateTime.now());
        r.setAuthor(user);
        this.reviewRepository.save(r);
    }

    //    페이징
    public Page<Review> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return this.reviewRepository.findAll(pageable);
    }
}
