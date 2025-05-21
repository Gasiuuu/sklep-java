// src/main/java/com/sklep/sklep_backend/service/MailService.java
package com.sklep.sklep_backend.service;

import jakarta.mail.MessagingException;

public interface MailService {
    void sendPlainText(String to, String subject, String text);
    void sendHtml(String to, String subject, String html) throws MessagingException;
}
