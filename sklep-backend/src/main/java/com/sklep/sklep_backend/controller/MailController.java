package com.sklep.sklep_backend.controller;

import com.sklep.sklep_backend.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/test")
    public ResponseEntity<String> sendTest(@RequestParam String to) {
        mailService.sendPlainText(to, "Hello from Spring Boot", "To działa! 🎉");
        return ResponseEntity.ok("Wysłano do " + to);
    }
}

