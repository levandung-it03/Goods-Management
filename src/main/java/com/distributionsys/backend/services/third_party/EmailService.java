package com.distributionsys.backend.services.third_party;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class EmailService {
    @Value("${spring.mail.username}")
    private String mailSender;
    private final JavaMailSender javaMailSender; //--Injection from configured bean in application.properties

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

            mimeMessageHelper.setFrom(mailSender);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.info("Exception Throw When Sending Mail: {}", e.toString());
        }
    }
}
