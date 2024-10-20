package com.market.allForOneReview.domain.article.Service;

import com.market.allForOneReview.DataNotFoundException;
import com.market.allForOneReview.domain.article.Repository.CategoryRepository;
import com.market.allForOneReview.domain.article.Repository.ReviewRepository;
import com.market.allForOneReview.domain.article.entity.Category;
import com.market.allForOneReview.domain.article.entity.Review;
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
    private final CategoryRepository categoryRepository;
    public void create(String title, String contentStory, String content, String category, String subCategory) {

        Optional<Category> category1 = this.categoryRepository.findByCategoryAndSubCategory(category,subCategory);


        Review r = new Review();
        r.setTitle(title);
        r.setContentStory(contentStory);
        r.setContent(content);
        r.setCreateDate(LocalDateTime.now());
        r.setCategory(category1.get());
        this.reviewRepository.save(r);
    }

    //    페이징
    public Page<Review> getReviewsByCategoryAndPage(String categoryName, int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이징 처리
        return reviewRepository.findByCategory_Category(categoryName, pageable);
    }

}
