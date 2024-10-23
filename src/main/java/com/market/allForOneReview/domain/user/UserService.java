package com.market.allForOneReview.domain.user;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.DisabledException;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SiteUser create(String username, String nickname, String password, String email) {
        SiteUser user = SiteUser.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .email(email)
                .verified(false)
                .enabled(false)
                .authority(1)  // 기본 사용자 권한 (예: 1=일반사용자, 2=관리자 등)
                .verificationCode(null)  // 초기에는 인증 코드 없음
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public SiteUser save(SiteUser user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public SiteUser findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    public SiteUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser user = findByUsername(username);

        if (!user.isVerified()) {
            throw new DisabledException("이메일 인증이 완료되지 않았습니다.");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("계정이 비활성화 상태입니다.");
        }

        // authority를 기반으로 권한 설정
        String role = user.getAuthority() == 2 ? "ADMIN" : "USER";
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                .build();
    }

    @Transactional
    public void verifyUser(String email, String verificationCode) {
        SiteUser user = findByEmail(email);
        if (verificationCode.equals(user.getVerificationCode())) {
            user.setVerified(true);
            user.setEnabled(true);
            user.setVerificationCode(null);  // 인증 완료 후 코드 제거
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("잘못된 인증 코드입니다.");
        }
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        SiteUser user = findByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void changeNickname(String username, String newNickname) {
        if (existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        SiteUser user = findByUsername(username);
        user.setNickname(newNickname);
        userRepository.save(user);
    }

    @Transactional
    public void setVerificationCode(String email, String verificationCode) {
        SiteUser user = findByEmail(email);
        user.setVerificationCode(verificationCode);
        userRepository.save(user);
    }

    @Transactional
    public void disableAccount(String username) {
        SiteUser user = findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableAccount(String username) {
        SiteUser user = findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public boolean checkPassword(String username, String password) {
        SiteUser user = findByUsername(username);
        return passwordEncoder.matches(password, user.getPassword());
    }
}