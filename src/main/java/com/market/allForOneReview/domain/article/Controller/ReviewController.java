package com.market.allForOneReview.domain.article.Controller;

import com.market.allForOneReview.domain.answer.AnswerForm;
import com.market.allForOneReview.domain.article.Service.ReviewService;
import com.market.allForOneReview.domain.article.entity.Category;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.article.entity.ReviewForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/sub")
    public String showReviews(
            @RequestParam(value="category", defaultValue="drama") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Page<Review> paging = reviewService.getReviewsByCategoryAndPage(category, page);
        model.addAttribute("paging", paging);
        model.addAttribute("currentCategory", category);
        model.addAttribute("reviews", paging.getContent());


        return "sub";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerForm answerForm) {
        Review review = this.reviewService.getReview(id);
        model.addAttribute("review", review);
        return "sub_detail";
    }

    @GetMapping("/create")
    public String reviewCreate(ReviewForm reviewForm) {
        return "create";
    }

    @PostMapping("/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        this.reviewService.create(reviewForm.getTitle(), reviewForm.getContentStory(), reviewForm.getContent(), reviewForm.getCategory(), reviewForm.getSubCategory());
        return "redirect:/review/sub";
    }

}
