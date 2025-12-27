package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/email")
    public ResponseEntity<String> testEmail(@RequestParam String to) {
        emailService.sendEmail(
                to,
                "Test Email System",
                "Email service is working correctly with OAuth2."
        );
        return ResponseEntity.ok("Email request submitted.");
    }
}