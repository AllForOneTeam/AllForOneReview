package com.market.allForOneReview.domain.auth.service;

import com.market.allForOneReview.domain.auth.AuthInfo;
import com.market.allForOneReview.domain.email.service.EmailService;
import com.market.allForOneReview.domain.user.repository.PasswordResetTokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private static final String AUTH_CODE_PREFIX = "AUTH:";

    @Value("${spring.auth.code.expiration-minutes}")
    private int authCodeExpirationMinutes;

    private final Map<String, AuthInfo> authCodeStore = new ConcurrentHashMap<>();

    @Transactional
    public void sendAuthCode(String email) throws MessagingException {
        String authCode = emailService.createAuthCode();
        String key = AUTH_CODE_PREFIX + email;

        // AuthInfo 생성자에 맞게 수정
        AuthInfo authInfo = new AuthInfo(authCode, email, authCodeExpirationMinutes);
        authCodeStore.put(key, authInfo);

        // 분 단위로 변환하여 전달
        int expirationMinutes = authCodeExpirationMinutes / (60 * 1000);
        emailService.sendVerificationEmail(email, authCode, expirationMinutes);
        log.info("Auth code sent to email: {}", email);
    }

    @Transactional
    public void sendPasswordResetEmail(String email) throws MessagingException {
        String resetToken = UUID.randomUUID().toString();
        int resetExpirationMinutes = 30; // 비밀번호 재설정 링크 30분 유효

        emailService.sendPasswordResetEmail(email, resetToken);

        // 토큰 저장 로직은 사용자 서비스에서 처리
        log.info("Password reset email sent to: {}", email);
    }

    public boolean verifyEmail(String email, String authCode) {
        String key = AUTH_CODE_PREFIX + email;
        AuthInfo authInfo = authCodeStore.get(key);

        if (authInfo == null) {
            log.warn("No auth code found for email: {}", email);
            return false;
        }

        if (authInfo.isExpired()) {
            log.warn("Auth code expired for email: {}", email);
            authCodeStore.remove(key);
            return false;
        }

        boolean isValid = authInfo.isValid(authCode);
        if (isValid) {
            authCodeStore.remove(key);
            log.info("Email verified successfully: {}", email);
        } else {
            log.warn("Invalid auth code for email: {}", email);
        }

        return isValid;
    }

    @Transactional(readOnly = true)
    public boolean isValidPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isExpired())
                .orElse(false);
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void cleanupExpiredCodes() {
        int beforeSize = authCodeStore.size();
        authCodeStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int afterSize = authCodeStore.size();

        if (beforeSize != afterSize) {
            log.info("Cleaned up {} expired auth codes", beforeSize - afterSize);
        }
    }

    public String getStoredAuthCode(String email) {
        AuthInfo authInfo = authCodeStore.get(AUTH_CODE_PREFIX + email);
        return authInfo != null ? authInfo.getCode() : null;
    }
}