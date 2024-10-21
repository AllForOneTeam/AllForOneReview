package com.market.allForOneReview.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StringRedisTemplate redisTemplate;
    private static final String AUTH_CODE_PREFIX = "AUTH:";

    public void saveAuthCode(String email, String authCode) {
        // 인증 코드를 Redis에 저장 (5분 동안 유효)
        redisTemplate.opsForValue()
                .set(AUTH_CODE_PREFIX + email, authCode, 5, TimeUnit.MINUTES);
    }

    public boolean verifyEmail(String email, String authCode) {
        String savedAuthCode = redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);

        if (savedAuthCode != null && savedAuthCode.equals(authCode)) {
            // 인증 성공 시 Redis에서 코드 삭제
            redisTemplate.delete(AUTH_CODE_PREFIX + email);
            return true;
        }
        return false;
    }
}