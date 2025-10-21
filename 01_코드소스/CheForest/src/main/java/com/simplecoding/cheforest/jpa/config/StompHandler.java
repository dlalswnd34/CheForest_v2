package com.simplecoding.cheforest.jpa.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            Optional<Principal> userOptional = Optional.ofNullable(accessor.getUser());

            if (userOptional.isPresent()) {
                Authentication auth = (Authentication) userOptional.get();
                accessor.setUser(auth);
                log.info("STOMP CONNECT ✅ 인증된 사용자 세션 연결: {}", auth.getName());
            } else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    accessor.setUser(auth);
                    log.info("STOMP CONNECT ✅ SecurityContext에서 인증정보 복원: {}", auth.getName());
                } else {
                    // 🌟 로그인 안한 사용자도 읽기전용으로 입장 가능
                    log.info("STOMP CONNECT 🟡 비로그인 사용자 (읽기전용)");
                    // Principal을 세팅하지 않고 그대로 통과
                }
            }
        }
        return message;
    }
}
