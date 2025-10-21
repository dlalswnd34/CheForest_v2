package com.simplecoding.cheforest.jpa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket 연결을 시작할 EndPoint를 설정합니다.
        // Handshake 과정에서 HTTP 세션 정보를 WebSocket 세션으로 안전하게 복사합니다.
        // 이 부분이 JSP의 new SockJS("/ws")와 일치해야 합니다.
        // 소셜 로그인 사용자도 세션 공유하도록 HttpSessionHandshakeInterceptor 추가
        registry.addEndpoint("/ws")
                .addInterceptors(new org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독(Subscribe)하는 클라이언트에게 메시지를 전달할 경로의 Prefix를 설정합니다.
        // JSP의 stompClient.subscribe("/sub/...") 부분과 일치합니다.
        registry.enableSimpleBroker("/sub");
        // 클라이언트가 서버로 메시지를 보낼(Publish) 경로의 Prefix를 설정합니다.
        // JSP의 stompClient.send("/pub/...") 부분과 일치합니다.
        registry.setApplicationDestinationPrefixes("/pub");
    }

    /**
     * 클라이언트로부터 들어오는 메시지를 처리하는 채널에 StompHandler를 등록합니다.
     * 이 핸들러가 Spring Security의 보안 검사보다 먼저 실행되어
     * CONNECT 메시지에 사용자 인증 정보를 올바르게 설정하는 핵심 역할을 합니다.
     */
    @Override
    public void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    /**
     * WebSocket 메시지에 대한 보안 규칙을 설정합니다.
     * StompHandler가 먼저 실행된 후 이 규칙이 적용됩니다.
     */
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                // "/pub/message" 경로로 들어오는 메시지는 인증된 사용자만 허용합니다.
                .simpDestMatchers("/pub/message").authenticated()
                // 구독은 누구나 허용 (읽기 공개)
                .simpSubscribeDestMatchers("/sub/**").permitAll()
                // 연결/심장박동/종료 프레임은 허용
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT, SimpMessageType.DISCONNECT).permitAll()
                // 그 외 다른 모든 메시지(CONNECT, SUBSCRIBE 등)는 인증된 사용자라면 모두 허용합니다.
                .anyMessage().authenticated();
    }

    /**
     * STOMP는 웹소켓을 사용하므로 일반적인 웹 CSRF 토큰을 사용하기 어렵습니다.
     * 비활성화하는 것이 일반적인 설정입니다.
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}

