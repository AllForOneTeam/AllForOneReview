package com.market.allForOneReview.domain.article.Controller;

import com.market.allForOneReview.domain.answer.AnswerForm;
import com.market.allForOneReview.domain.article.Service.ReviewService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/sub")
    public String review(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Review> paging = this.reviewService.getList(page);
        model.addAttribute("paging", paging);
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
    public String reviewCreate() {
        return "create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.reviewService.create(reviewForm.getTitle(), reviewForm.getContentStory(), reviewForm.getContent(), siteUser);
        return "redirect:/review/sub";
    }

}
