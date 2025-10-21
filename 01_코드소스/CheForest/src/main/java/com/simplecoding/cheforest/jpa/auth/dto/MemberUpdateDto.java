package com.simplecoding.cheforest.jpa.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberUpdateDto {
    @NotBlank
    private String nickname;

    private String profile;
}
