package com.simplecoding.cheforest.jpa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberSignupDto {

    @NotBlank
    @Size(min = 8, message = "아이디는 최소 8자 이상이어야 합니다.")
    private String loginId;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]).{10,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해 10자 이상이어야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    private String confirmPassword;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String nickname;

    private String emailAuthCode; // 사용자가 입력한 인증번호
}
