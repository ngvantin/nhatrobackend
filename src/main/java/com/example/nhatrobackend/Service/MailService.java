package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.Deposit;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.Room;
import com.example.nhatrobackend.Entity.User;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
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
        helper.setFrom(emailFrom, "Trọ Tốt");

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
     * Send notification email with flexible content
     */
    public void sendNotificationEmail(String emailTo, String title, String header, String content, String buttonText, String buttonUrl) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending notification email to user, email={}, title={}", emailTo, title);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();

        // Set variables for template
        Map<String, Object> properties = new HashMap<>();
        properties.put("title", title);
        properties.put("header", header);
        properties.put("content", content);
        properties.put("buttonText", buttonText);
        properties.put("buttonUrl", buttonUrl);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Trọ Tốt");
        helper.setTo(emailTo);
        helper.setSubject(title);
        String html = templateEngine.process("notification-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Notification email has been sent to user, email={}, title={}", emailTo, title);
    }

    /**
     * Send reset password link.
     */
    public void sendResetPasswordLink(String emailTo, String resetToken) throws MessagingException, UnsupportedEncodingException {
        String linkConfirm = String.format("%s/reset-password?secretKey=%s", serverName, resetToken);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();

        // Set variables for template
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Trọ Tốt");
        helper.setTo(emailTo);
        helper.setSubject("Xác nhận đặt lại mật khẩu");
        String html = templateEngine.process("reset-password-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Reset password email has been sent to user, email={}", emailTo);
    }

    /**
     * Send confirmation link for registration
     */
    public void sendConfirmLinkRegister(String emailTo, String resetToken) throws MessagingException, UnsupportedEncodingException {
        String linkConfirm = String.format("%s/register?secretKey=%s", serverName, resetToken);
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();

        // Set variables for template
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Trọ Tốt");
        helper.setTo(emailTo);
        helper.setSubject("Xác nhận tài khoản");
        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Confirmation email has been sent to user, email={}", emailTo);
    }

    /**
     * Send post approval notification
     */
    public void sendPostApprovedNotification(String emailTo, String postTitle, String postUrl) throws MessagingException, UnsupportedEncodingException {
        sendNotificationEmail(
            emailTo,
            "Bài đăng của bạn đã được duyệt",
            "Chúc mừng! Bài đăng của bạn đã được duyệt",
            String.format("Bài đăng \"%s\" của bạn đã được phê duyệt và đã được đăng lên hệ thống.", postTitle),
            "Xem bài đăng",
            postUrl
        );
    }

    /**
     * Send post rejection notification
     */
    public void sendPostRejectedNotification(String emailTo, String postTitle, String postUrl) throws MessagingException, UnsupportedEncodingException {
        sendNotificationEmail(
            emailTo,
            "Bài đăng của bạn đã bị từ chối",
            "Thông báo về bài đăng của bạn",
            String.format("Bài đăng \"%s\" của bạn đã bị từ chối. Vui lòng kiểm tra và chỉnh sửa lại nội dung.", postTitle),
            "Xem bài đăng",
            postUrl
        );
    }

    /**
     * Send landlord approval notification
     */
    public void sendLandlordApprovedNotification(String emailTo) throws MessagingException, UnsupportedEncodingException {
        sendNotificationEmail(
            emailTo,
            "Tài khoản chủ trọ đã được duyệt",
            "Chúc mừng! Tài khoản chủ trọ của bạn đã được duyệt",
            "Tài khoản chủ trọ của bạn đã được phê duyệt. Bạn có thể bắt đầu đăng bài cho thuê.",
            "Đăng bài mới",
            String.format("%s/posts/create", serverName)
        );
    }

    /**
     * Send landlord rejection notification
     */
    public void sendLandlordRejectedNotification(String emailTo) throws MessagingException, UnsupportedEncodingException {
        sendNotificationEmail(
            emailTo,
            "Tài khoản chủ trọ đã bị từ chối",
            "Thông báo về tài khoản chủ trọ",
            "Đơn đăng ký tài khoản chủ trọ của bạn đã bị từ chối. Vui lòng kiểm tra lại thông tin và thử lại.",
            "Đăng ký lại",
            String.format("%s/landlord/register", serverName)
        );
    }

    /**
     * Send new post notification to a list of followers.
     */
    public void sendNewPostNotificationToFollowers(List<String> followerEmails, String authorName, String postTitle, String postUrl) {
        log.info("Sending new post notification email to {} followers of {}", followerEmails.size(), authorName);
        String title = authorName + " đã đăng bài viết mới";
        String header = "Có bài viết mới từ " + authorName;
        String content = String.format("Người bạn theo dõi, %s, vừa đăng bài viết mới \"%s\".", authorName, postTitle);
        String buttonText = "Xem bài viết";

        for (String emailTo : followerEmails) {
            try {
                sendNotificationEmail(
                    emailTo,
                    title,
                    header,
                    content,
                    buttonText,
                    postUrl
                );
                log.debug("Sent new post email to follower: {}", emailTo);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Failed to send new post email to follower {}: {}", emailTo, e.getMessage(), e);
                // Continue to the next email even if one fails
            }
        }
        log.info("Finished sending new post notification emails.");
    }

    /**
     * Send notification to users with matching search criteria
     */
    public void sendMatchingPostNotification(List<String> matchingUserEmails, String postTitle, String postUrl, String location) {
        log.info("Sending matching post notification email to {} users", matchingUserEmails.size());
        String title = "Có phòng trọ phù hợp với yêu cầu của bạn";
        String header = "Phòng trọ mới phù hợp với yêu cầu tìm kiếm";
        String content = String.format("Chúng tôi tìm thấy phòng trọ \"%s\" tại %s phù hợp với yêu cầu tìm kiếm của bạn.", postTitle, location);
        String buttonText = "Xem chi tiết";

        for (String emailTo : matchingUserEmails) {
            try {
                sendNotificationEmail(
                    emailTo,
                    title,
                    header,
                    content,
                    buttonText,
                    postUrl
                );
                log.debug("Sent matching post email to user: {}", emailTo);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Failed to send matching post email to user {}: {}", emailTo, e.getMessage(), e);
                // Continue to the next email even if one fails
            }
        }
        log.info("Finished sending matching post notification emails.");
    }

    /**
     * Send deposit success notification to both tenant and landlord
     */
    public void sendDepositSuccessNotification(Deposit deposit) throws MessagingException, UnsupportedEncodingException {
        Post post = deposit.getPost();
        Room room = post.getRoom();
        User tenant = deposit.getUser();
        User landlord = post.getUser();

        // Format room address
        String roomAddress = String.format("%s %s, %s, %s, %s",
                room.getHouseNumber(),
                room.getStreet(),
                room.getWard(),
                room.getDistrict(),
                room.getCity());

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String depositTime = deposit.getCreatedAt().format(formatter);

        // Prepare email data
        Map<String, Object> properties = new HashMap<>();
        properties.put("postTitle", post.getTitle());
        properties.put("roomAddress", roomAddress);
        properties.put("roomArea", room.getArea());
        properties.put("roomPrice", String.format("%,.0f", room.getPrice()));
        properties.put("furnitureStatus", room.getFurnitureStatus().getValue());
        properties.put("electricityPrice", String.format("%,.0f", room.getElectricityPrice()));
        properties.put("waterPrice", String.format("%,.0f", room.getWaterPrice()));
        properties.put("depositId", deposit.getDepositId());
        properties.put("depositAmount", String.format("%,.0f", deposit.getAmount()));
        properties.put("depositTime", depositTime);
        properties.put("landlordName", landlord.getFullName());
        properties.put("landlordPhone", landlord.getPhoneNumber());
        properties.put("tenantName", tenant.getFullName());
        properties.put("tenantPhone", tenant.getPhoneNumber());

        // Send email to tenant
        if (tenant.getEmail() != null) {
            sendDepositSuccessEmail(tenant.getEmail(), properties);
        }

        // Send email to landlord
        if (landlord.getEmail() != null) {
            sendDepositSuccessEmail(landlord.getEmail(), properties);
        }
    }

    /**
     * Send refund success notification to tenant
     */
    public void sendRefundSuccessNotification(Deposit deposit) throws MessagingException, UnsupportedEncodingException {
        Post post = deposit.getPost();
        Room room = post.getRoom();
        User tenant = deposit.getUser();
        User landlord = post.getUser();

        // Format room address
        String roomAddress = String.format("%s %s, %s, %s, %s",
                room.getHouseNumber(),
                room.getStreet(),
                room.getWard(),
                room.getDistrict(),
                room.getCity());

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String refundTime = deposit.getUpdatedAt().format(formatter);

        // Prepare email data
        Map<String, Object> properties = new HashMap<>();
        properties.put("postTitle", post.getTitle());
        properties.put("roomAddress", roomAddress);
        properties.put("roomArea", room.getArea());
        properties.put("roomPrice", String.format("%,.0f", room.getPrice()));
        properties.put("depositId", deposit.getDepositId());
        properties.put("refundAmount", String.format("%,.0f", deposit.getRefundAmount()));
        properties.put("refundTime", refundTime);
        properties.put("landlordName", landlord.getFullName());
        properties.put("landlordPhone", landlord.getPhoneNumber());
        properties.put("tenantName", tenant.getFullName());
        properties.put("tenantPhone", tenant.getPhoneNumber());

        // Send email to tenant
        if (tenant.getEmail() != null) {
            sendRefundSuccessEmail(tenant.getEmail(), properties);
        }
    }

    /**
     * Send commission payment notification to landlord
     */
    public void sendCommissionPaymentNotification(Deposit deposit, double landlordAmount, double commissionAmount) throws MessagingException, UnsupportedEncodingException {
        Post post = deposit.getPost();
        Room room = post.getRoom();
        User tenant = deposit.getUser();
        User landlord = post.getUser();

        // Format room address
        String roomAddress = String.format("%s %s, %s, %s, %s",
                room.getHouseNumber(),
                room.getStreet(),
                room.getWard(),
                room.getDistrict(),
                room.getCity());

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String paymentTime = LocalDateTime.now().format(formatter);

        // Prepare email data
        Map<String, Object> properties = new HashMap<>();
        properties.put("postTitle", post.getTitle());
        properties.put("roomAddress", roomAddress);
        properties.put("roomArea", room.getArea());
        properties.put("roomPrice", String.format("%,.0f", room.getPrice()));
        properties.put("depositId", deposit.getDepositId());
        properties.put("depositAmount", String.format("%,.0f", deposit.getAmount()));
        properties.put("landlordAmount", String.format("%,.0f", landlordAmount));
        properties.put("commissionAmount", String.format("%,.0f", commissionAmount));
        properties.put("paymentTime", paymentTime);
        properties.put("landlordName", landlord.getFullName());
        properties.put("landlordPhone", landlord.getPhoneNumber());
        properties.put("tenantName", tenant.getFullName());
        properties.put("tenantPhone", tenant.getPhoneNumber());

        // Send email to landlord
        if (landlord.getEmail() != null) {
            sendCommissionPaymentEmail(landlord.getEmail(), properties);
        }
    }

    private void sendDepositSuccessEmail(String emailTo, Map<String, Object> properties) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Trọ Tốt");
        helper.setTo(emailTo);
        helper.setSubject("Thông báo đặt cọc thành công");
        String html = templateEngine.process("deposit-success-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Deposit success notification email has been sent to: {}", emailTo);
    }

    private void sendRefundSuccessEmail(String emailTo, Map<String, Object> properties) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Trọ Tốt");
        helper.setTo(emailTo);
        helper.setSubject("Thông báo hoàn tiền đặt cọc");
        String html = templateEngine.process("refund-success-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Refund success notification email has been sent to: {}", emailTo);
    }

    private void sendCommissionPaymentEmail(String emailTo, Map<String, Object> properties) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Trọ Tốt");
        helper.setTo(emailTo);
        helper.setSubject("Thông báo thanh toán hoa hồng");
        String html = templateEngine.process("commission-success-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Commission payment notification email has been sent to: {}", emailTo);
    }

    /**
     * Send account locked notification
     */
    public void sendAccountLockedNotification(String emailTo) throws MessagingException, UnsupportedEncodingException {
        sendNotificationEmail(
            emailTo,
            "Tài khoản của bạn đã bị khóa",
            "Thông báo khóa tài khoản",
            "Tài khoản của bạn đã bị khóa do vi phạm điều khoản sử dụng. Vui lòng liên hệ với chúng tôi qua email hoặc số điện thoại hotline để được hỗ trợ.",
            "Liên hệ hỗ trợ",
            String.format("%s/contact", serverName)
        );
    }

    /**
     * Send report-locked post notification to user
     */
    public void sendReportLockedPostEmail(User user, String postTitle, String reason, String adminResponse, LocalDateTime reportTime) throws MessagingException, UnsupportedEncodingException {
        if (user.getEmail() == null) return;
        Map<String, Object> properties = new HashMap<>();
        properties.put("postTitle", postTitle);
        properties.put("reason", reason);
        properties.put("adminResponse", adminResponse);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        properties.put("reportTime", reportTime.format(formatter));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Trọ Tốt");
        helper.setTo(user.getEmail());
        helper.setSubject("Thông báo bài đăng bị khóa do báo cáo");
        String html = templateEngine.process("report-locked-post-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Report-locked post notification email has been sent to: {}", user.getEmail());
    }
}
