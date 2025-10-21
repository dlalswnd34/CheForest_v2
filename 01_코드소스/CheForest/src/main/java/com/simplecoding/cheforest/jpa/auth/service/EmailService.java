package com.simplecoding.cheforest.jpa.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증번호 생성
    private String createCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자
        return String.valueOf(code);
    }

    // 인증 메일 발송
    public String sendAuthCode(String to) {
        String code = createCode();
        String subject = "[CheForest] 이메일 인증번호 안내";
        String content = "<h3>인증번호는 <b>" + code + "</b> 입니다.</h3>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("cheforest6@gmail.com", "CheForest");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }

        return code; // 컨트롤러에서 세션에 저장
    }

    // 뉴스레터 구독 메일 (추가)
    public void sendNewsletterMail(String to) {
        String subject = "[CheForest] 뉴스레터 구독이 완료되었습니다!";
        String content = """
            <div style="font-family: Pretendard, Apple SD Gothic Neo, sans-serif; line-height:1.6">
              <h2>🍳 CheForest 뉴스레터 구독 완료!</h2>
              <p>이제부터 매주 맛있는 레시피와 이벤트 소식을 이메일로 받아보실 수 있습니다.</p>
              <p>감사합니다.<br>오늘도 행복한 요리 되세요!</p>
              <hr style="border:none;border-top:1px solid #eee;margin:20px 0"/>
              <p style="font-size:12px;color:#999">※ 수신을 원치 않으시면 1:1문의를 이용해주세요.</p>
            </div>
        """;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("cheforest6@gmail.com", "CheForest");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("뉴스레터 메일 발송 실패", e);
        }
    }
}
