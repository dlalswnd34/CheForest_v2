package com.simplecoding.cheforest.jpa.auth.dto;

import java.util.Date;

public interface MemberAdminDto {
    Long getMemberIdx();
    String getEmail();
    String getRole();
    String getNickname();
    Date getInsertTime();
    String getProfile();
    String getSocialId();
    String getProvider();
    Date getUpdateTime();
    Long getPoint();
    String getGrade();
    String getStatus();
    Date getLastLoginTime();
    Long getBoardCount();
    Long getBoardReviewCount();
}
