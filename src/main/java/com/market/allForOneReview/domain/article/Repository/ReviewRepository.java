package com.market.allForOneReview.domain.article.Repository;

import com.market.allForOneReview.domain.article.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByCategory_Category(String category, Pageable pageable);

}