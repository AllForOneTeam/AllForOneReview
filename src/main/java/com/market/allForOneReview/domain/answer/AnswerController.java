package com.market.allForOneReview.domain.answer;

import com.market.allForOneReview.domain.article.Service.ReviewService;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.user.service.UserService;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final ReviewService reviewService;
    private final AnswerService answerService;
    private final UserService userService;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Long id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        Review review = this.reviewService.getReview(id);
        SiteUser siteUser = this.userService.findByUsername(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("review", review);
            return "sub_detail";
        }
        this.answerService.create(review, answerForm.getContent(), siteUser);
        return String.format("redirect:/review/detail/%s", id);
    }
}
