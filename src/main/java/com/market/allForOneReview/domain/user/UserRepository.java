package com.market.allForOneReview.domain.user;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
}
