package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.Service.MailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/common")
public record CommonController(MailService mailService) {

    @PostMapping("/send-email")
    public ResponseEntity<ResponseWrapper<String>> sendEmail(@RequestParam String recipients, @RequestParam String subject,
                                     @RequestParam String content, @RequestParam(required = false) MultipartFile[] files) {
        log.info("Request GET /common/send-email");
        try {
            mailService.sendEmail(recipients, subject, content, files);
            return ResponseEntity.ok(ResponseWrapper.<String>builder()
                    .status("success")
                    .message("Gửi email thành công")
                    .build());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Sending email was failure, message={}", e.getMessage(), e);
            return ResponseEntity.ok(ResponseWrapper.<String>builder()
                    .status("success")
                    .message("Gửi email thất bại")
                    .build());
        }
    }
}
