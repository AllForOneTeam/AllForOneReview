package com.market.allForOneReview.domain.auth.controller;

import com.market.allForOneReview.domain.auth.service.AuthService;
import com.market.allForOneReview.domain.user.UserService;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/auth")
    public String getAuthPage(@RequestParam(value = "error", required = false) Boolean error,
                              @RequestParam(value = "verified", required = false) Boolean verified,
                              Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("errorMessage", "인증번호가 일치하지 않거나 만료되었습니다.");
        }
        if (Boolean.TRUE.equals(verified)) {
            model.addAttribute("successMessage", "이메일 인증이 완료되었습니다.");
        }
        return "member/auth";
    }

    @PostMapping("/auth")
    public String verifyEmail(@RequestParam String email,
                              @RequestParam String authNumber,
                              Model model, RedirectAttributes redirectAttributes) {
        try {
            // 사용자 확인
            SiteUser user = userService.findByEmail(email);
            try {
                user = userService.findByEmail(email);
            } catch (UsernameNotFoundException e) {
                model.addAttribute("error", "등록되지 않은 이메일입니다.");
                return "member/auth";
            }

            // 인증번호 검증
            boolean isVerified = authService.verifyEmail(email, authNumber);

            if (isVerified) {
                log.info("Email verification successful for: {}", email);
                // 인증 성공 시 사용자 상태 업데이트
                user.setVerified(true);
                user.setEnabled(true);  // 계정 활성화
                userService.save(user);

                // 성공 메시지를 리다이렉트 시 전달
                redirectAttributes.addFlashAttribute("message",
                        "이메일 인증이 완료되었습니다. 로그인해주세요.");
                return "redirect:/login";
            } else {
                log.warn("Email verification failed for: {}", email);
                model.addAttribute("error", "인증번호가 일치하지 않거나 만료되었습니다.");
                return "member/auth";
            }

        } catch (Exception e) {
            log.error("Error during email verification", e);
            model.addAttribute("error", "인증 처리 중 오류가 발생했습니다.");
            return "member/auth";
        }
    }

    @PostMapping("/auth/send")
    @ResponseBody
    public ResponseEntity<String> sendAuthCode(@RequestParam(name = "email") String email) {
        try {
            // 사용자 존재 여부 확인
            SiteUser user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("등록되지 않은 이메일입니다.");
            }

            authService.sendAuthCode(email);
            return ResponseEntity.ok("인증 코드가 발송되었습니다.");
        } catch (Exception e) {
            log.error("Failed to send auth code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("인증 코드 발송에 실패했습니다.");
        }
    }
}