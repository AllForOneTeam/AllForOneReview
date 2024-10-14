package com.market.allForOneReview.domain.article.Repository;

import com.market.allForOneReview.domain.article.entity.ReviewPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPostRepository extends JpaRepository<ReviewPost,Long> {
}
