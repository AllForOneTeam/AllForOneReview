package com.market.allForOneReview.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    @Size(min = 4, max = 20)
    @NotEmpty(message = "아이디는 필수입력 사항입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문 대소문자와 숫자만 사용 가능합니다.")
    private String userId;

    @Size(min = 3, max = 20)
    @NotEmpty(message = "닉네임을 필수입력 사항입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
    private String nickname;

    @Size(max = 20)
    @NotEmpty(message = "비밀번호는 필수입력 사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\w\\S])[A-Za-z\\d\\w\\S]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8자리 이상이어야 합니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수입력 사항입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수입력 사항입니다.")
    @Email
    private String email;
}