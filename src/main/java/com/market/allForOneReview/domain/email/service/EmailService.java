package com.market.allForOneReview.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // @Value 어노테이션 경로 수정
    @Value("${spring.auth.code.expiration-millis}")
    private long authCodeExpirationMillis;

    @Value("${spring.mail.username}")  // mail.username 값을 주입받음
    private String senderEmail;

    // 인증 코드를 메모리에 저장할 Map (key: 이메일, value: 인증 코드)
    private final Map<String, String> authCodeStore = new HashMap<>();

    // 랜덤으로 숫자 생성
    public String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) { // 인증 코드 8자리
            int index = random.nextInt(3); // 0~2까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 소문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 2 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    // 메일 생성
    public MimeMessage createMail(String recipientEmail, String number) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(senderEmail);  // mail.username에서 주입된 값 사용
        message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
        message.setSubject("이메일 인증");

        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>감사합니다.</h3>";

        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 메일 발송
    public String sendSimpleMessage(String recipientEmail) throws MessagingException {
        String number = createNumber(); // 랜덤 인증번호 생성
        MimeMessage message = createMail(recipientEmail, number); // 메일 생성

        try {
            mailSender.send(message); // 메일 발송
            // 생성된 인증번호를 메모리에 저장
            saveAuthCode(recipientEmail, number);
        } catch (MailException e) {
            e.printStackTrace();
            throw new MailSendException("메일 발송 중 오류가 발생했습니다.");
        }

        return number; // 생성된 인증번호 반환
    }

    // 인증 코드를 메모리에 저장
    public void saveAuthCode(String email, String code) {
        authCodeStore.put(email, code); // 메모리에 저장
    }

    // 인증 코드를 확인하는 메서드
    public boolean verifyAuthCode(String email, String code) {
        String storedCode = authCodeStore.get(email);
        return storedCode != null && storedCode.equals(code); // 저장된 코드와 비교
    }
}
