package com.market.allForOneReview.domain.user;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);

        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                });

        // 이메일 인증 확인
        if (!siteUser.isVerified()) {
            log.warn("User {} is not verified", username);
            throw new DisabledException("이메일 인증이 완료되지 않았습니다.");
        }

        // 계정 활성화 상태 확인
        if (!siteUser.isEnabled()) {
            log.warn("User {} is not enabled", username);
            throw new DisabledException("계정이 비활성화 상태입니다.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (siteUser.getAuthority() == 2) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
            log.debug("User {} has ADMIN role", username);
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
            log.debug("User {} has USER role", username);
        }

        log.info("User {} successfully authenticated", username);
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }
}