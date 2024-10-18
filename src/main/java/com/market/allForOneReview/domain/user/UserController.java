package com.market.allForOneReview.domain.user;

import com.market.allForOneReview.domain.email.service.EmailService;
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
    private final EmailService emailService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/membership")
    public String signup(UserCreateForm userCreateForm) {
        return "membership";
    }

    @PostMapping("/membership")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        // 폼 검증에 에러가 있으면 바로 회원가입 페이지로 돌아감
        if (bindingResult.hasErrors()) {
            return "membership";
        }

        // 패스워드 일치 여부 검증
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "membership";
        }

        // username 중복 확인
        if (userService.existsByUsername(userCreateForm.getUsername())) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자 ID 입니다.");
            return "membership";
        }

        // email 중복 확인
        if (userService.existsByEmail(userCreateForm.getEmail())) {
            bindingResult.reject("signupFailed", "이미 등록된 이메일 입니다.");
            return "membership";
        }

        // 닉네임 중복 확인
        if (userService.existsByNickname(userCreateForm.getNickname())) {
            bindingResult.reject("signupFailed", "중복된 닉네임 입니다.");
            return "membership";
        }

        try {
            // 유저 생성
            this.userService.create(userCreateForm.getUsername(), userCreateForm.getNickname(), userCreateForm.getPassword1(), userCreateForm.getEmail());
            emailService.send(userCreateForm.getEmail(), "서비스 가입을 환영합니다!", "회원가입 환영 메일");
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
            return "membership";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "membership";
        }

        // 회원가입 성공 시 메인 페이지로 리다이렉트
        return "redirect:/";
    }
}
