package com.yes.asyncmessage.utils;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderUtil {

    private final JavaMailSender javaMailSender;

    public EmailSenderUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // 发送简单文本邮件
    public void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        // 设置发件人
        message.setFrom("3220244679@qq.com");
        javaMailSender.send(message);
    }
}