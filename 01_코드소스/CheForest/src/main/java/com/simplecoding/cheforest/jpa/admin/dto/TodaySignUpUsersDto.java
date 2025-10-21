package com.simplecoding.cheforest.jpa.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TodaySignUpUsersDto {
    private Long memberIdx;
    private String id;
    private String password;
    private String email;
    private String role;
    private String nickname;
    private Date insertTime;
    private String profile;
}
