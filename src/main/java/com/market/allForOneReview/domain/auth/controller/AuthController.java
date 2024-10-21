package com.market.allForOneReview.domain.auth.controller;

import com.market.allForOneReview.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth")
    public String verifyEmail(@RequestParam("email") String email,
                              @RequestParam("authNumber") String authNumber) {

        boolean isVerified = authService.verifyEmail(email, authNumber);

        if (isVerified) {
            return "redirect:/login?verified=true";
        } else {
            return "redirect:/auth?error=true";
        }
    }
}