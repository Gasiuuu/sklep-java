//package com.sklep.sklep_backend.service.impl;
//
////package com.sklep.sklep_backend.mail;
//
//import com.sklep.sklep_backend.service.MailService;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MailServiceImpl implements MailService {
//
//    private final JavaMailSender mailSender;
//
//    public void sendPlainText(String to, String subject, String text) {
//        mailSender.send(m -> {
//            m.setFrom("your@gmail.com");
//            m.setRecipients(MimeMessage.RecipientType.TO, to);
//            m.setSubject(subject);
//            m.setText(text);
//        });
//    }
//
//    public void sendHtml(String to, String subject, String html) throws MessagingException {
//        MimeMessage mime = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mime, "utf-8");
//        helper.setFrom("your@gmail.com");
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(html, true);      // true => HTML
//        mailSender.send(mime);
//    }
//}


// src/main/java/com/sklep/sklep_backend/service/impl/MailServiceImpl.java
package com.sklep.sklep_backend.service.impl;

import com.sklep.sklep_backend.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private static final String FROM = "your@gmail.com";

    @Override
    public void sendPlainText(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(FROM);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    @Override
    public void sendHtml(String to, String subject, String html) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, "utf-8");
        helper.setFrom(FROM);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);  // true = HTML
        mailSender.send(mime);
    }
}
