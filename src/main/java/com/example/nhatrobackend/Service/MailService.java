package com.example.nhatrobackend.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${spring.application.serverName}")
    private String serverName;

    public String sendEmail(String recipients, String subject, String content, MultipartFile[] files) throws UnsupportedEncodingException, MessagingException {
        log.info("Email is sending ...");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(emailFrom, "Nhà Trọ Rẻ");

        if (recipients.contains(",")) { // send to multiple users
            helper.setTo(InternetAddress.parse(recipients));
        } else { // send to single user
            helper.setTo(recipients);
        }

        // Send attach files
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

        log.info("Email has sent to successfully, recipients: {}", recipients);

        return "Sent";
    }

    /**
     * Send link confirm to email register.
     *
     * @param emailTo
     * @param resetToken
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendConfirmLink(String emailTo, String resetToken) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirming link to user, email={}", emailTo);

        MimeMessage message = mailSender.createMimeMessage(); // Đại diện cho một email

        //  cung cấp các phương thức thuận tiện để thiết lập các thuộc tính cơ bản của email
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context(); // Context từ Thymeleaf một template

        String linkConfirm = String.format("%s/reset-password?secretKey=%s", serverName, resetToken); // serverName sẽ thay thế placeholder %s đầu tiên

        // lưu trữ các cặp key-value
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Nhà Trọ Rẻ"); // Dòng này đặt địa chỉ email người gửi và tên người gửi cho email.
        helper.setTo(emailTo);
        helper.setSubject("Please confirm your account");
        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Confirming link has sent to user, email={}, linkConfirm={}", emailTo, linkConfirm);
    }

    public void sendConfirmLinkRegister(String emailTo, String resetToken) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirming link to user, email={}", emailTo);

        MimeMessage message = mailSender.createMimeMessage(); // Đại diện cho một email

        //  cung cấp các phương thức thuận tiện để thiết lập các thuộc tính cơ bản của email
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context(); // Context từ Thymeleaf một template

        String linkConfirm = String.format("%s/register?secretKey=%s", serverName, resetToken); // serverName sẽ thay thế placeholder %s đầu tiên

        // lưu trữ các cặp key-value
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Nhà Trọ Rẻ"); // Dòng này đặt địa chỉ email người gửi và tên người gửi cho email.
        helper.setTo(emailTo);
        helper.setSubject("Please confirm your account");
        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Confirming link has sent to user, email={}, linkConfirm={}", emailTo, linkConfirm);
    }
}
