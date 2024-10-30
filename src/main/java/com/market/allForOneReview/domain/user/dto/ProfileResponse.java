package com.market.allForOneReview.domain.user.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProfileResponse {
    private String nickname;
    private String profileImageUrl;
    private int followingCount;
    private int postCount;
    private int commentCount;
}