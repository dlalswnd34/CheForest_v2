package com.simplecoding.cheforest.jpa.auth.security;

import com.simplecoding.cheforest.jpa.auth.entity.Member;

public interface AuthUser {  // 소셜회원과 일반회원의 맴버값을 동일시하기위해 만듬
    Member getMember();
}