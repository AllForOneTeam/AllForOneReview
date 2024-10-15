package com.market.allForOneReview.domain.article.Controller;

import com.market.allForOneReview.domain.article.Service.ReviewPostService;
import com.market.allForOneReview.domain.article.entity.ReviewPost;
import com.market.allForOneReview.domain.article.entity.ReviewPostForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewPostController {
    private final ReviewPostService reviewPostService;

    @GetMapping("/sub")
    public String sub() {
        return "sub";
    }

    @GetMapping("/detail")
    public String detail() {
        return "sub_detail";
    }

    @GetMapping("/create")
    public String create(ReviewPostForm reviewPostForm){
        return "create";
    }

    @PostMapping("/create")
    public String createReviewPost(@Valid ReviewPostForm reviewPostForm, BindingResult bindingResult) {
        if( bindingResult.hasErrors()){
            return "create";
        }
        this.reviewPostService.create(reviewPostForm.getTitle(), reviewPostForm.getContent(), reviewPostForm.getContentStory());
        return "redirect:/review/list";
    }

}
