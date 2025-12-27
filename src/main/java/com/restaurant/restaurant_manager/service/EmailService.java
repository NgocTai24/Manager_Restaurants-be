package com.restaurant.restaurant_manager.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${google.mail.client-id}")
    private String clientId;

    @Value("${google.mail.client-secret}")
    private String clientSecret;

    @Value("${google.mail.refresh-token}")
    private String refreshToken;


    private String getAccessToken() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setClientSecrets(clientId, clientSecret)
                    .build();

            credential.setRefreshToken(refreshToken);
            credential.refreshToken();

            return credential.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh access token: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo JavaMailSender với cấu hình OAuth2
     */
    private JavaMailSenderImpl getAuthenticatedMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(emailFrom);

        // Mật khẩu chính là Access Token vừa lấy
        mailSender.setPassword(getAccessToken());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        // Các cấu hình fix lỗi kết nối/EOF
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // FIX LỖI TÊN MÁY TÍNH TIẾNG VIỆT (QUAN TRỌNG)
        props.put("mail.smtp.localhost", "localhost");

        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

        props.put("mail.debug", "true");

        return mailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {

            // 1. Lấy Sender đã xác thực
            JavaMailSenderImpl sender = getAuthenticatedMailSender();

            // 2. Tạo Message
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            // 3. Gửi
            sender.send(message);
            System.out.println("✓ Email sent successfully to: " + to);

        } catch (Exception e) {
            System.err.println("✗ Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}