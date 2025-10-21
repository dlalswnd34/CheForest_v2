package com.simplecoding.cheforest.jpa.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 에러 메세지를 가져와서 사용하는 클래스
 * 에러 메세지: messages.properties
 */
@Component
@RequiredArgsConstructor
public class ErrorMsg {

    private final MessageSource messageSource;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
