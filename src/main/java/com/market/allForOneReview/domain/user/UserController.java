package com.market.allForOneReview.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/membership")
    public String signup(UserCreateForm userCreateForm) {
        return "membership";
    }

    @PostMapping("/membership")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "membership";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "membership";
        }

        try {
            userService.create(userCreateForm.getUserId(), userCreateForm.getNickname(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            String errorMessage = e.getMostSpecificCause().getMessage();
            if (errorMessage.contains("user_id")) {
                bindingResult.rejectValue("userId", "duplicate", "이미 등록된 사용자입니다.");
            } else if (errorMessage.contains("email")) {
                bindingResult.rejectValue("email", "duplicate", "이미 등록된 사용자입니다.");
            } else if (errorMessage.contains("nickname")) {
                bindingResult.rejectValue("nickname", "duplicate", "같은 닉네임이 이미 존재합니다.");
            } else {
                bindingResult.reject("signupFailed", "회원가입 중 오류가 발생했습니다.");
            }
            return "membership";
        } catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "membership";
        }

        return "redirect:/";
    }
}
