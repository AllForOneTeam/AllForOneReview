package com.market.allForOneReview.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JavaMailSender mailSender;

    @Value("${spring.auth.code.expiration-millis}")
    private long authCodeExpirationMillis;

    private static final String AUTH_CODE_PREFIX = "AUTH:";

    private static class AuthInfo {
        private final String code;
        private final LocalDateTime expiryTime;
        private final String email;

        public AuthInfo(String code, String email, long expirationMillis) {
            this.code = code;
            this.email = email;
            this.expiryTime = LocalDateTime.now().plus(Duration.ofMillis(expirationMillis));
        }

        public boolean isValid(String inputCode) {
            return !isExpired() && code.equals(inputCode);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    private final Map<String, AuthInfo> authCodeStore = new ConcurrentHashMap<>();

    // 인증 코드 생성 및 이메일 발송
    @Transactional
    public void sendAuthCode(String email) {
        String authCode = generateAuthCode();
        saveAuthCode(email, authCode);
        sendEmail(email, authCode);
    }

    private String generateAuthCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void saveAuthCode(String email, String authCode) {
        log.info("Saving auth code for email: {}", email);
        String key = AUTH_CODE_PREFIX + email;
        authCodeStore.put(key, new AuthInfo(authCode, email, authCodeExpirationMillis));
    }

    private void sendEmail(String email, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + authCode + "\n\n이 코드는 5분 동안 유효합니다.");

        try {
            mailSender.send(message);
            log.info("Auth code email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send auth code email to: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    public boolean verifyEmail(String email, String authCode) {
        log.info("Verifying email: {}", email);
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
            log.info("Auth code verified successfully for email: {}", email);
            authCodeStore.remove(key);
        } else {
            log.warn("Invalid auth code provided for email: {}", email);
        }

        return isValid;
    }

    // 만료된 인증 코드 정리
    public void cleanupExpiredCodes() {
        authCodeStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}