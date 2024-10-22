package com.market.allForOneReview.domain.article.Controller;

import com.market.allForOneReview.domain.answer.AnswerForm;
import com.market.allForOneReview.domain.article.Service.ReviewService;
import com.market.allForOneReview.domain.article.entity.Category;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.article.entity.ReviewForm;
import com.market.allForOneReview.domain.user.UserService;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class  ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

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

    @GetMapping("/sub/search")
    public String searchReviews(@RequestParam("filter") String filter,
                                @RequestParam("query") String query,
                                @RequestParam(value="category", defaultValue="drama") String category,
                                @RequestParam(value = "subCategory", defaultValue = "all") String subCategory,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                Model model) {
        Page<Review> paging = reviewService.searchReviews(filter, query, "drama", subCategory, page);
        model.addAttribute("paging", paging);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentSubCategory", subCategory);
        model.addAttribute("filter", filter);
        model.addAttribute("query", query);
        model.addAttribute("reviews", paging.getContent());
        return "sub";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerForm answerForm) {
        Review review = this.reviewService.getReview(id);
        model.addAttribute("review", review);
        return "sub_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String reviewCreate(ReviewForm reviewForm) {
        return "create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.reviewService.create(reviewForm.getTitle(), reviewForm.getContentStory(), reviewForm.getContent(), reviewForm.getCategory(), reviewForm.getSubCategory(), siteUser);

        return "redirect:/review/sub";
    }

}
