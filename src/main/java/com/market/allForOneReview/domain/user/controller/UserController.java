package com.market.allForOneReview.domain.user.controller;

import com.market.allForOneReview.domain.auth.service.AuthService;
import com.market.allForOneReview.domain.user.UserCreateForm;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;  // EmailService 대신 AuthService 주입

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/membership")
    public String signup(UserCreateForm userCreateForm) {
        return "member/membership";
    }

    @PostMapping("/membership")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, Model model) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return "member/membership";
        }

        // 기존의 validation 체크들...
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "member/membership";
        }

        if (userService.existsByUsername(userCreateForm.getUsername())) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자 ID 입니다.");
            return "member/membership";
        }

        if (userService.existsByEmail(userCreateForm.getEmail())) {
            bindingResult.reject("signupFailed", "이미 등록된 이메일 입니다.");
            return "member/membership";
        }

        if (userService.existsByNickname(userCreateForm.getNickname())) {
            bindingResult.reject("signupFailed", "중복된 닉네임 입니다.");
            return "member/membership";
        }

        try {
            // 1. 유저 생성
            SiteUser user = userService.create(
                    userCreateForm.getUsername(),
                    userCreateForm.getNickname(),
                    userCreateForm.getPassword1(),
                    userCreateForm.getEmail()
            );

            // 2. 인증 코드 생성 및 이메일 발송
            authService.sendAuthCode(user.getEmail());

            // 3. 저장된 인증 코드 가져오기
            String storedAuthCode = authService.getStoredAuthCode(user.getEmail());

            // 4. 데이터베이스에 인증 코드 저장
            userService.setVerificationCode(user.getEmail(), storedAuthCode);

            log.info("User created and verification code sent to: {}", user.getEmail());

            // 5. 인증 페이지로 필요한 정보 전달
            model.addAttribute("email", user.getEmail());

        } catch (DataIntegrityViolationException e) {
            log.error("회원가입 중 데이터 무결성 위반 오류 발생", e);
            bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
            return "member/membership";
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생", e);
            bindingResult.reject("signupFailed", e.getMessage());
            return "member/membership";
        }

        return "member/auth";
    }

    // 아이디/비밀번호 찾기 페이지 보여주기
    @GetMapping("/find-account")
    public String findAccount() {
        return "member/find_account";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username,
                                @RequestParam String email,
                                Model model) {
        try {
            // 사용자 정보 확인
            SiteUser user = userService.findByUsername(username);
            if (!user.getEmail().equals(email)) {
                throw new IllegalArgumentException("이메일 주소가 일치하지 않습니다.");
            }

            // 비밀번호 재설정 이메일 발송
            authService.sendPasswordResetEmail(email);

            model.addAttribute("success", "비밀번호 재설정 링크가 이메일로 발송되었습니다.");
            return "member/find_account";
        } catch (Exception e) {
            log.error("Failed to process password reset request", e);
            model.addAttribute("error", "비밀번호 재설정 이메일 발송에 실패했습니다.");
            return "member/find_account";
        }
    }

    @GetMapping("/reset-password/{token}")
    public String showResetPasswordForm(@PathVariable String token, Model model) {
        if (authService.isValidPasswordResetToken(token)) {
            model.addAttribute("token", token);
            return "member/reset_password";
        }
        return "redirect:/user/find-account?error=invalid_token";
    }

    @PostMapping("/reset-password/{token}")
    public String processResetPassword(@PathVariable String token,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       Model model) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            userService.resetPassword(token, newPassword);
            return "redirect:/user/login?reset=success";
        } catch (Exception e) {
            log.error("Failed to reset password", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "member/reset_password";
        }
    }
}