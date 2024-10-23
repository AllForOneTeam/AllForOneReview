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

        // getter 추가
        public String getCode() {
            return code;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }

    private final Map<String, AuthInfo> authCodeStore = new ConcurrentHashMap<>();

    @Transactional
    public void sendAuthCode(String email) {
        try {
            String authCode = generateAuthCode();
            saveAuthCode(email, authCode);
            sendEmail(email, authCode);
            log.info("Auth code generation and sending completed for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to process auth code for email: {}", email, e);
            throw new RuntimeException("인증 코드 처리 중 오류가 발생했습니다.", e);
        }
    }

    private String generateAuthCode() {
        Random random = new Random();
        String authCode = String.format("%06d", random.nextInt(1000000));
        log.debug("Generated auth code: {}", authCode);
        return authCode;
    }

    private void saveAuthCode(String email, String authCode) {
        try {
            String key = AUTH_CODE_PREFIX + email;
            AuthInfo previousAuth = authCodeStore.get(key);
            if (previousAuth != null) {
                log.info("Removing previous auth code for email: {}", email);
                authCodeStore.remove(key);
            }

            authCodeStore.put(key, new AuthInfo(authCode, email, authCodeExpirationMillis));
            log.info("Auth code saved successfully for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to save auth code for email: {}", email, e);
            throw new RuntimeException("인증 코드 저장 중 오류가 발생했습니다.", e);
        }
    }

    private void sendEmail(String email, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText(String.format(
                "인증 코드: %s\n\n이 코드는 %d분 동안 유효합니다.",
                authCode,
                authCodeExpirationMillis / (60 * 1000)
        ));

        try {
            mailSender.send(message);
            log.info("Auth code email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send auth code email to: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    public boolean verifyEmail(String email, String authCode) {
        try {
            String key = AUTH_CODE_PREFIX + email;
            AuthInfo authInfo = authCodeStore.get(key);

            if (authInfo == null) {
                log.warn("No auth code found for email: {}", email);
                return false;
            }

            log.debug("Stored auth code: {}, Provided auth code: {}, Expiry time: {}",
                    authInfo.getCode(), authCode, authInfo.getExpiryTime());

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

        } catch (Exception e) {
            log.error("Error during email verification for: {}", email, e);
            return false;
        }
    }

    // 주기적으로 실행되어야 하는 만료된 코드 정리 메소드
    public void cleanupExpiredCodes() {
        try {
            int beforeSize = authCodeStore.size();
            authCodeStore.entrySet().removeIf(entry -> {
                boolean expired = entry.getValue().isExpired();
                if (expired) {
                    log.debug("Removing expired auth code for email: {}",
                            entry.getValue().email);
                }
                return expired;
            });
            int afterSize = authCodeStore.size();

            if (beforeSize != afterSize) {
                log.info("Cleaned up {} expired auth codes", beforeSize - afterSize);
            }
        } catch (Exception e) {
            log.error("Error during cleanup of expired auth codes", e);
        }
    }

    // 디버깅용 메소드
    public boolean hasStoredAuthCode(String email) {
        String key = AUTH_CODE_PREFIX + email;
        return authCodeStore.containsKey(key);
    }

    // 저장된 인증 코드를 반환하는 메소드 추가
    public String getStoredAuthCode(String email) {
        String key = AUTH_CODE_PREFIX + email;
        AuthInfo authInfo = authCodeStore.get(key);
        return authInfo != null ? authInfo.getCode() : null;
    }
}