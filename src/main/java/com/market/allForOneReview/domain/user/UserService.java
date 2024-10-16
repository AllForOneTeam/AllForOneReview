package com.market.allForOneReview.domain.user;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String userId, String nickname, String password, String email) {
        SiteUser user = SiteUser.builder()
                        .userId(userId)
                        .nickname(nickname)
                        .password(passwordEncoder.encode(password))
                        .email(email)
                        .build();

        return userRepository.save(user);
    }

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
