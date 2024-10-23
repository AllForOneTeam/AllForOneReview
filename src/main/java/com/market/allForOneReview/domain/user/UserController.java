package com.market.allForOneReview.domain.user;

import com.market.allForOneReview.domain.auth.service.AuthService;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}