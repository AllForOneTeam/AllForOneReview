package com.market.allForOneReview.domain.article.Service;

import com.market.allForOneReview.domain.article.Repository.ReviewPostRepository;
import com.market.allForOneReview.domain.article.entity.ReviewPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;

    public void create(String Title, String content, String contentStory){
        ReviewPost reviewPost = new ReviewPost();
        reviewPost.setTitle(Title);
        reviewPost.setContent(content);
        reviewPost.setContentStory(contentStory);
    }
}
