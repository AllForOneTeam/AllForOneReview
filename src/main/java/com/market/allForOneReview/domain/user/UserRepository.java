package com.market.allForOneReview.domain.user;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<SiteUser> findByUserId(String userId);
}
