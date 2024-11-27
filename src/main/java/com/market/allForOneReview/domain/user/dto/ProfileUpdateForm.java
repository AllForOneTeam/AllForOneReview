package com.market.allForOneReview.domain.user.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileUpdateForm {
    private String nickname;
    private MultipartFile profileImage;
}