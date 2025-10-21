    package com.simplecoding.cheforest.jpa.auth.security.oauth;

    public interface OAuth2UserInfo {
        String getId();
        String getName();
        String getEmail();
        String getImageUrl();
    }
