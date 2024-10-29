package com.market.allForOneReview.domain.notice.controller;

import com.market.allForOneReview.domain.notice.service.CommentService;
import com.market.allForOneReview.domain.notice.dto.CommentForm;
import com.market.allForOneReview.domain.notice.entity.NoticePost;
import com.market.allForOneReview.domain.notice.service.NoticePostService;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final NoticePostService noticePostService;
    private final CommentService commentService;
    private final UserService userService;


    @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Long id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal){
        NoticePost noticePost = this.noticePostService.getNotice(id);
        SiteUser siteUser = this.userService.findByUsername(principal.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("notice", noticePost);
            return "notice/notice_detail";
        }
        this.commentService.create(noticePost,commentForm.getContent(), siteUser);
        return String.format("redirect:/notice/detail/%s" , id);
    }

}
