package com.market.allForOneReview.domain.notice.controller;

import com.market.allForOneReview.domain.notice.dto.CommentForm;
import com.market.allForOneReview.domain.notice.dto.NoticeForm;
import com.market.allForOneReview.domain.notice.entity.NoticePost;

import com.market.allForOneReview.domain.notice.service.NoticePostService;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticePostController {
    private final NoticePostService noticePostService;
    private final UserService userService;

    @GetMapping("")
    public String notice(
            @RequestParam(value="boardType", defaultValue="Notice") String boardType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        // 공통된 처리 로직
        Page<NoticePost> paging = noticePostService.getNoticesByBoardTypeAndPage(boardType, page);

        model.addAttribute("paging", paging);
        model.addAttribute("currentBoardType", boardType);

        // boardType에 따라 다른 데이터를 추가
        if (boardType.equals("FAQ")) {
            model.addAttribute("faqs", paging.getContent());
        } else {
            model.addAttribute("notices", paging.getContent());
        }

        return "notice/notice";
    }


    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id")Long id, CommentForm commentForm) {
        NoticePost noticePost = this.noticePostService.getNotice(id);
        model.addAttribute("notice", noticePost);
        return "notice/notice_detail";
    }

    @GetMapping("/create")
    public String create(NoticeForm noticeform){
        return "notice/notice_create";
    }

    @PostMapping("/create")
    public String create(@Valid NoticeForm noticeForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return"notice_create";
        }
//        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.noticePostService.create(noticeForm.getTitle(), noticeForm.getContent(), noticeForm.getBoardType());
        return "redirect:/notice";
    }
    @GetMapping("/modify/{id}")
    public String noticeModify(NoticeForm noticeForm, @PathVariable("id")Long id){
        NoticePost notice = this.noticePostService.getNotice(id);
        noticeForm.setTitle(notice.getTitle());
        noticeForm.setContent(notice.getContent());
        return "notice/notice_create";
    }
    @PostMapping("/modify/{id}")
    public String noticeModify(@Valid NoticeForm noticeForm, BindingResult bindingResult, @PathVariable("id") Long id){
        if (bindingResult.hasErrors()) {
            return "notice/notice_create";
        }
        NoticePost notice = this.noticePostService.getNotice(id);
        this.noticePostService.modify(notice, noticeForm.getTitle(), noticeForm.getContent());
        return String.format("redirect:/notice/detail/%s",id);
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id")Long id){
        NoticePost notice = this.noticePostService.getNotice(id);
        this.noticePostService.delete(notice);
        return "redirect:/notice";
    }

}
